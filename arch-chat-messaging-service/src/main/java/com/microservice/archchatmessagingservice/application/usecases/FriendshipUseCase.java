package com.microservice.archchatmessagingservice.application.usecases;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.microservice.archchatmessagingservice.application.exceptions.*;
import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.application.gateways.MessagePublisherGateway;
import com.microservice.archchatmessagingservice.application.usecases.dto.*;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;

import com.microservice.archchatmessagingservice.infrastructure.messaging.dto.NotificationEventDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FriendshipUseCase {

    private final FriendshipRepositoryGateway friendshipRepositoryGateway;
    private final MessagePublisherGateway messagePublisherGateway;

    public Friendship sendFriendRequest(SendFriendRequestInput input) {
        UUID requester = input.requesterId();
        UUID receiver = input.receiverId();

        if (requester.equals(receiver)) {
            throw new InvalidSelfActionException("Não é possível enviar uma solicitação de amizade para si mesmo");
        }

        Optional<Friendship> existingRelation = friendshipRepositoryGateway.findRelationBetween(requester, receiver);

        if (existingRelation.isPresent()) {
            Friendship relation = existingRelation.get();

            boolean blockedByUser = (relation.getRequesterId().equals(requester) && relation.isBlockedByRequester()) ||
                    (relation.getReceiverId().equals(requester) && relation.isBlockedByReceiver());

            boolean blockedByFriend = (relation.getRequesterId().equals(receiver) && relation.isBlockedByRequester()) ||
                    (relation.getReceiverId().equals(receiver) && relation.isBlockedByReceiver());

            if (relation.getStatus() == FriendshipStatus.ACCEPTED) {
                throw new AlreadyAreFriendsException("Vocês já são amigos");
            }

            if (relation.getStatus() == FriendshipStatus.PENDING) {
                if (relation.getRequesterId().equals(requester)) {
                    throw new RequestAlreadyPendingException("Você já enviou uma solicitação de amizade para esse usuário");
                } else {
                    throw new RequestAlreadyPendingException("Este usuário já te enviou uma solicitação de amizade");
                }
            }

            if (relation.getStatus() == FriendshipStatus.BLOCKED) {
                if (blockedByUser) {
                    throw new UserBlockedException("Você bloqueou este usuário. Desbloqueie-o primeiro");
                }
                if (blockedByFriend) {
                    throw new UserBlockedException("Não foi possível enviar a solicitação de amizade");
                }
            }

            if (relation.getStatus() == FriendshipStatus.DECLINED) {
                relation.setStatus(FriendshipStatus.PENDING);
                relation.setRequesterId(requester);
                relation.setReceiverId(receiver);
                relation.setBlockedByRequester(false);
                relation.setBlockedByReceiver(false);
                relation.setCreatedAt(LocalDateTime.now());
            }

            return friendshipRepositoryGateway.save(relation);
        }

        Friendship newRequest = Friendship.builder()
                .requesterId(requester)
                .receiverId(receiver)
                .status(FriendshipStatus.PENDING)
                .blockedByRequester(false)
                .blockedByReceiver(false)
                .createdAt(LocalDateTime.now())
                .build();

        Friendship saved = friendshipRepositoryGateway.save(newRequest);

        messagePublisherGateway.publishNotification(new NotificationEventDto(
                requester,
                receiver,
                null,
                "FRIEND_REQUEST",
                "Você recebeu uma nova solicitação de amizade!",
                LocalDateTime.now()
        ));

        return saved;
    }

    public Friendship declineRequest(DeclineFriendRequestInput input){
        Friendship friendship = friendshipRepositoryGateway.findById(input.friendshipId())
                .orElseThrow(() -> new FriendshipNotFoundException("Solicitação de amizade não encontrada"));

        if(friendship.getStatus() != FriendshipStatus.PENDING){
            throw new InvalidFriendshipStateException("Esta solicitação não está pendente");
        }

        if(!input.receiverId().equals(friendship.getReceiverId())){
            throw new UnauthorizedActionException("Você não tem permissão para recusar essa solicitação");
        }

        friendship.setStatus(FriendshipStatus.DECLINED);

        return friendshipRepositoryGateway.save(friendship);
    }

    public Friendship acceptRequest(AcceptFriendRequestInput input){
        Friendship friendship = friendshipRepositoryGateway.findById(input.friendshipId())
                .orElseThrow(() -> new FriendshipNotFoundException("Solicitação de amizade não encontrada"));

        if(friendship.getStatus() != FriendshipStatus.PENDING){
            throw new InvalidFriendshipStateException("Esta solicitação não está pendente");
        }

        if(!input.receiverId().equals(friendship.getReceiverId())){
            throw new UnauthorizedActionException("Você não tem permissão para aceitar essa solicitação");
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);

        return friendshipRepositoryGateway.save(friendship);
    }

    public Friendship blockUser(BlockUserInput input){
        if(input.blockedId().equals(input.blockerId())){
            throw new InvalidSelfActionException("Não é possível bloquear a sí mesmo");
        }

        Optional<Friendship> existingRelation = friendshipRepositoryGateway.findRelationBetween(input.blockedId(), input.blockerId());

        if(existingRelation.isPresent()){
            Friendship relation = existingRelation.get();

            if (relation.getRequesterId().equals(input.blockerId())) {
                relation.setBlockedByRequester(true);
            } else {
                relation.setBlockedByReceiver(true);
            }

            relation.setStatus(FriendshipStatus.BLOCKED);
            return friendshipRepositoryGateway.save(relation);
        }

        return friendshipRepositoryGateway.save(Friendship.builder()
                .requesterId(input.blockerId())
                .receiverId(input.blockedId())
                .status(FriendshipStatus.BLOCKED)
                .blockedByRequester(true)
                .blockedByReceiver(false)
                .createdAt(LocalDateTime.now())
                .build());

    }

    public Friendship unblockUser(UnblockUserInput input){

        Friendship relation = friendshipRepositoryGateway.findRelationBetween(input.unblockerId(), input.blockedId())
                .orElseThrow(() -> new FriendshipNotFoundException("Nenhum relacionamento encontrado para realizar o desbloqueio"));

        if(relation.getStatus() != FriendshipStatus.BLOCKED){
            throw new InvalidFriendshipStateException("Este relacionamento não está bloqueado");
        }

        if(input.unblockerId().equals(relation.getRequesterId())){
            if(!relation.isBlockedByRequester()){
                throw new UnauthorizedActionException("Você não bloqueou este usuário");
            }
            relation.setBlockedByRequester(false);
        } else {
            if(!relation.isBlockedByReceiver()){
                throw new UnauthorizedActionException("Você não bloqueou este usuário");
            }
            relation.setBlockedByReceiver(false);
        }

        if (!relation.isBlockedByRequester() && !relation.isBlockedByReceiver()) {
            relation.setStatus(FriendshipStatus.DECLINED);
        }

        return friendshipRepositoryGateway.save(relation);
    }

    public List<UUID> getAcceptedFriendships(UUID userId){

        var friendships = friendshipRepositoryGateway.findAcceptedFriendshipsByUserId(userId);

        return friendships.stream()
                .map(f -> f.getRequesterId().equals(userId) ? f.getReceiverId() : f.getRequesterId())
                .toList();
    }

    public List<Friendship> getPendingRequests(UUID userId) {
        return friendshipRepositoryGateway.findPendingRequests(userId);
    }

}

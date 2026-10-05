package com.microservice.archchatmessagingservice.infrastructure.gateways;

import com.microservice.archchatmessagingservice.application.gateways.FriendshipRepositoryGateway;
import com.microservice.archchatmessagingservice.domain.Friendship;
import com.microservice.archchatmessagingservice.domain.enums.FriendshipStatus;
import com.microservice.archchatmessagingservice.infrastructure.persistence.DataFriendshipRepository;
import com.microservice.archchatmessagingservice.infrastructure.persistence.entities.FriendshipEntity;
import com.microservice.archchatmessagingservice.infrastructure.persistence.mappers.FriendshipMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class FriendshipRepositoryGatewayImpl implements FriendshipRepositoryGateway {

    private final FriendshipMapper mapper;
    private final DataFriendshipRepository repository;

    @Override
    public Friendship save(Friendship friendship) {
        FriendshipEntity entity = mapper.toEntity(friendship);
        FriendshipEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Friendship> findById(UUID id) {
        return repository.findById(id)
                .map(entity -> mapper.toDomain(entity));
    }

    @Override
    public Optional<Friendship> findRelationBetween(UUID user1, UUID user2) {
        return repository.findRelationBetween(user1, user2)
                .map(entity -> mapper.toDomain(entity));
    }

    @Override
    public List<Friendship> findAcceptedFriendshipsByUserId(UUID userId) {
        return repository.findAcceptedFriendshipsByUserId(userId)
                .stream()
                .map(entity -> mapper.toDomain(entity))
                .toList();
    }

    @Override
    public List<Friendship> findPendingRequests(UUID receiverId) {
        return repository.findByReceiverIdAndStatus(receiverId, FriendshipStatus.PENDING).stream()
                .map(mapper::toDomain)
                .toList();
    }
}

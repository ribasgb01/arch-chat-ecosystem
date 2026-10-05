import { Component, signal, inject, OnInit, OnDestroy, ElementRef, ViewChild, effect } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { StompSubscription } from '@stomp/stompjs';
import { AuthService } from '../../core/services/auth';
import { FriendshipService, UserSearch } from '../../core/services/friendship';
import { ChatService, ChatItem, MessageItem } from '../../core/services/chat';
import { WebSocketService } from '../../core/services/websocket';
import { CallService } from '../../core/services/call';
import { NotificationService, SystemNotification } from '../../core/services/notification';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [FormsModule, DatePipe],
  templateUrl: './chat.html'
})
export class ChatComponent implements OnInit, OnDestroy {
  private router = inject(Router);
  private authService = inject(AuthService);
  private friendshipService = inject(FriendshipService);
  private chatService = inject(ChatService);
  private wsService = inject(WebSocketService);
  private notificationService = inject(NotificationService);
  public callService = inject(CallService);

  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;
  @ViewChild('fileInput') private fileInput!: ElementRef;
  @ViewChild('localVideo') private localVideo!: ElementRef<HTMLVideoElement>;
  @ViewChild('remoteVideo') private remoteVideo!: ElementRef<HTMLVideoElement>;

  currentUser = signal(this.authService.getCurrentUser());
  friendsMap = signal<Map<string, UserSearch>>(new Map());

  acceptedFriendIds = signal<Set<string>>(new Set());
  searchQuery = signal('');
  searchResults = signal<UserSearch[]>([]);
  sentRequests = signal<string[]>([]);
  isSearching = signal(false);

  pendingRequests = signal<any[]>([]);
  systemNotifications = signal<SystemNotification[]>([]);
  showNotificationModal = signal(false);
  activeNotifTab = signal<'FRIENDS' | 'SYSTEM'>('FRIENDS');

  chats = signal<ChatItem[]>([]);
  selectedChat = signal<ChatItem | null>(null);

  showGroupModal = signal(false);
  groupName = signal('');
  selectedGroupMembers = signal<string[]>([]);

  showBlockedModal = signal(false);
  blockedUsers = signal<UserSearch[]>(
    JSON.parse(localStorage.getItem('blockedUsers') || '[]')
  );

  messages = signal<MessageItem[]>([]);
  messageInput = signal('');
  private chatSubscription: StompSubscription | null = null;
  editingMessage = signal<MessageItem | null>(null);
  attachmentUrls = signal<Map<string, string>>(new Map());

  isRecording = signal(false);
  private mediaRecorder: MediaRecorder | null = null;
  private audioChunks: Blob[] = [];
  private recordingStartTime = 0;

  constructor() {
    effect(() => {
      const local = this.callService.localStream();
      const remote = this.callService.remoteStream();

      if (this.localVideo && local) {
        this.localVideo.nativeElement.srcObject = local;
      }
      if (this.remoteVideo && remote) {
        this.remoteVideo.nativeElement.srcObject = remote;
      }
    });
  }

  ngOnInit() {
    this.loadFriends();
    this.loadPendingRequests();
    this.loadSystemNotifications();
    this.loadChats();

    const token = localStorage.getItem('token');
    if (token && this.currentUser()) {
      this.wsService.connect(token, () => console.log('Chat WS OK'));
      this.callService.connect(token, this.currentUser()!.userId);
    }
  }

  ngOnDestroy() {
    if (this.chatSubscription) this.chatSubscription.unsubscribe();
    this.wsService.disconnect();
    this.callService.hangUp();
  }

  loadPendingRequests() {
    this.friendshipService.getPendingRequests().subscribe({
      next: (reqs) => this.pendingRequests.set(reqs)
    });
  }

  loadSystemNotifications() {
    this.notificationService.getNotifications().subscribe({
      next: (res) => this.systemNotifications.set(res.content)
    });
  }

  markNotificationAsRead(notif: SystemNotification) {
    if (notif.read) return;
    this.notificationService.markAsRead(notif.id).subscribe({
      next: () => {
        this.systemNotifications.update(list =>
          list.map(n => n.id === notif.id ? { ...n, read: true } : n)
        );
      }
    });
  }

  loadChats() {
    this.chatService.getChats().subscribe({
      next: (chats) => {
        this.chats.set(chats);
        chats.forEach(chat => {
          if (chat.type === 'DIRECT') {
            const friendId = chat.participantIds.find(id => id !== this.currentUser()?.userId);
            if (friendId && !this.friendsMap().has(friendId)) {
              this.friendshipService.getUserById(friendId).subscribe({
                next: (u) => this.friendsMap.update(map => new Map(map.set(friendId, u)))
              });
            }
          }
        });
      }
    });
  }

  loadFriends() {
    this.friendshipService.getAcceptedFriends().subscribe({
      next: (friendIds) => {
        this.acceptedFriendIds.set(new Set(friendIds));

        friendIds.forEach(id => {
          if (!this.friendsMap().has(id)) {
            this.friendshipService.getUserById(id).subscribe({
              next: (u) => this.friendsMap.update(map => new Map(map.set(id, u)))
            });
          }
        });
      }
    });
  }

  getChatName(chat: ChatItem): string {
    if (chat.name) return chat.name;
    const friendId = chat.participantIds.find(id => id !== this.currentUser()?.userId);
    if (friendId && this.friendsMap().has(friendId)) {
      return this.friendsMap().get(friendId)!.username;
    }
    return 'Carregando...';
  }

  getFriendsList(): { id: string; user: UserSearch }[] {
    const list: { id: string; user: UserSearch }[] = [];
    this.friendsMap().forEach((user, id) => list.push({ id, user }));
    return list;
  }

  selectChat(chat: ChatItem) {
    this.selectedChat.set(chat);
    this.cancelEdit();

    this.chatService.getChatHistory(chat.id).subscribe({
      next: (res) => {
        const msgs = [...res.content].reverse();
        this.messages.set(msgs);
        this.scrollToBottom();
        msgs.forEach(msg => {
          if (msg.attachment) this.loadAttachmentUrl(msg.id);
        });
      }
    });

    if (this.chatSubscription) this.chatSubscription.unsubscribe();

    this.chatSubscription = this.wsService.subscribeToChat(chat.id, (incomingMsg: MessageItem) => {
      this.messages.update(prev => {
        const index = prev.findIndex(m => m.id === incomingMsg.id);
        if (index !== -1) {
          const updated = [...prev];
          updated[index] = incomingMsg;
          return updated;
        }
        return [...prev, incomingMsg];
      });

      if (incomingMsg.attachment) this.loadAttachmentUrl(incomingMsg.id);
      this.scrollToBottom();
    });
  }

  deleteCurrentChat() {
    if (!this.selectedChat()) return;
    const isGroup = this.selectedChat()!.type === 'GROUP';
    const message = isGroup 
      ? 'Tem certeza que deseja apagar este grupo e todas as mensagens?' 
      : 'Tem certeza que deseja apagar todo o histórico desta conversa?';

    if (confirm(message)) {
      this.chatService.deleteChat(this.selectedChat()!.id).subscribe({
        next: () => {
          this.chats.update(prev => prev.filter(c => c.id !== this.selectedChat()!.id));
          this.selectedChat.set(null);
          alert(isGroup ? 'Grupo excluído com sucesso.' : 'Conversa excluída com sucesso.');
        },
        error: (err) => alert(err.error?.error || 'Erro ao excluir chat')
      });
    }
  }

  acceptFriend(request: any) {
    this.friendshipService.acceptRequest(request.id).subscribe({
      next: () => {
        this.pendingRequests.update(prev => prev.filter(r => r.id !== request.id));
        this.acceptedFriendIds.update(set => new Set(set.add(request.requesterId)));
        this.loadChats();
      }
    });
  }

  declineFriend(request: any) {
    this.friendshipService.declineRequest(request.id).subscribe({
      next: () => {
        this.pendingRequests.update(prev => prev.filter(r => r.id !== request.id));
      },
      error: (err) => alert(err.error?.error || 'Erro ao recusar amizade')
    });
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        localStorage.removeItem('token');
        this.router.navigate(['/login']);
      },
      error: () => {
        localStorage.removeItem('token');
        this.router.navigate(['/login']);
      }
    });
  }

  openGroupModal() {
    this.groupName.set('');
    this.selectedGroupMembers.set([]);
    this.showGroupModal.set(true);
  }

  toggleGroupMember(friendId: string) {
    this.selectedGroupMembers.update(members =>
      members.includes(friendId) ? members.filter(id => id !== friendId) : [...members, friendId]
    );
  }

  createGroup() {
    if (!this.groupName().trim() || this.selectedGroupMembers().length === 0) {
      alert('Informe o nome do grupo e selecione pelo menos 1 amigo.');
      return;
    }
    this.chatService.createGroupChat(this.groupName(), this.selectedGroupMembers()).subscribe({
      next: (newGroup) => {
        this.chats.update(prev => [newGroup, ...prev]);
        this.selectChat(newGroup);
        this.showGroupModal.set(false);
      }
    });
  }

  blockCurrentFriend() {
    if (!this.selectedChat()) return;
    const friendId = this.selectedChat()!.participantIds.find(id => id !== this.currentUser()?.userId);
    if (friendId && confirm('Deseja bloquear este usuário?')) {
      this.friendshipService.blockUser(friendId).subscribe({
        next: () => {
          const friend = this.friendsMap().get(friendId);
          if (friend) {
            this.blockedUsers.update(prev => [...prev.filter(u => u.id !== friendId), friend]);
            localStorage.setItem('blockedUsers', JSON.stringify(this.blockedUsers()));
          }
          this.chats.update(prev => prev.filter(c => c.id !== this.selectedChat()!.id));
          this.selectedChat.set(null);
        }
      });
    }
  }

  unblockUser(user: UserSearch) {
    this.friendshipService.unblockUser(user.id).subscribe({
      next: () => {
        this.blockedUsers.update(prev => prev.filter(u => u.id !== user.id));
        localStorage.setItem('blockedUsers', JSON.stringify(this.blockedUsers()));
      }
    });
  }

  startVideoCall() {
    if (!this.selectedChat()) return;
    const friendId = this.selectedChat()!.participantIds.find(id => id !== this.currentUser()?.userId);
    if (friendId) this.callService.startCall(friendId);
  }

  loadAttachmentUrl(messageId: string) {
    if (!this.selectedChat() || this.attachmentUrls().has(messageId)) return;
    this.chatService.getAttachmentUrl(this.selectedChat()!.id, messageId).subscribe({
      next: (res) => this.attachmentUrls.update(map => new Map(map.set(messageId, res.url)))
    });
  }

  triggerFileInput() { this.fileInput.nativeElement.click(); }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file && this.selectedChat()) {
      this.chatService.sendFile(this.selectedChat()!.id, file).subscribe({
        next: () => this.fileInput.nativeElement.value = ''
      });
    }
  }

  async toggleAudioRecording() {
    if (this.isRecording()) {
      this.mediaRecorder?.stop();
      this.isRecording.set(false);
    } else {
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        this.audioChunks = [];
        this.mediaRecorder = new MediaRecorder(stream);
        this.recordingStartTime = Date.now();
        this.mediaRecorder.ondataavailable = (e) => this.audioChunks.push(e.data);
        this.mediaRecorder.onstop = () => {
          const duration = Math.round((Date.now() - this.recordingStartTime) / 1000);
          const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });
          if (this.selectedChat()) {
            this.chatService.sendAudio(this.selectedChat()!.id, audioBlob, duration).subscribe();
          }
          stream.getTracks().forEach(t => t.stop());
        };
        this.mediaRecorder.start();
        this.isRecording.set(true);
      } catch {
        alert('Permissão de microfone negada.');
      }
    }
  }

  sendMessage() {
    const text = this.messageInput().trim();
    if (!text || !this.selectedChat()) return;
    if (this.editingMessage()) {
      this.wsService.editMessage(this.selectedChat()!.id, this.editingMessage()!.id, text);
      this.cancelEdit();
    } else {
      this.wsService.sendMessage(this.selectedChat()!.id, text);
      this.messageInput.set('');
    }
  }

  startEdit(msg: MessageItem) {
    this.editingMessage.set(msg);
    this.messageInput.set(msg.content);
  }

  cancelEdit() {
    this.editingMessage.set(null);
    this.messageInput.set('');
  }

  deleteMessage(msg: MessageItem) {
    if (confirm('Deseja apagar esta mensagem?')) {
      this.wsService.deleteMessage(this.selectedChat()!.id, msg.id);
    }
  }

  private scrollToBottom() {
    setTimeout(() => {
      if (this.scrollContainer) {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
      }
    }, 100);
  }

  onSearchInput(query: string) {
    this.searchQuery.set(query);
    if (query.trim().length < 2) {
      this.searchResults.set([]);
      return;
    }
    this.isSearching.set(true);
    this.friendshipService.searchUsers(query).subscribe({
      next: (results) => {
        const myId = this.currentUser()?.userId;
        this.searchResults.set(results.filter(u => u.id !== myId));
        this.isSearching.set(false);
      }
    });
  }

  addFriend(user: UserSearch) {
    this.friendshipService.sendFriendRequest(user.id).subscribe({
      next: () => this.sentRequests.update(prev => [...prev, user.id])
    });
  }

  isAlreadyFriend(userId: string): boolean {
    return this.acceptedFriendIds().has(userId);
  }

  startChatWithFriend(friendId: string) {
    this.chatService.createDirectChat(friendId).subscribe({
      next: (chat) => {
        if (!this.chats().some(c => c.id === chat.id)) {
          this.chats.update(prev => [chat, ...prev]);
        }
        this.selectChat(chat);
        this.searchQuery.set('');
        this.searchResults.set([]);
      },
      error: (err) => alert(err.error?.error || 'Erro ao abrir conversa')
    });
  }
}
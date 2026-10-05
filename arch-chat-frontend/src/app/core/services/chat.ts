import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface MessageItem {
  id: string;
  chatId: string;
  senderId: string;
  content: string;
  timestamp: string;
  isEdited: boolean;
  status: string;
  type: string;
  attachment?: any;
}


export interface ChatItem {
  id: string;
  name: string | null;
  type: 'DIRECT' | 'GROUP';
  participantIds: string[];
  lastMessage?: {
    content: string;
    timestamp: string;
  };
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);
  private chatsUrl = '/api/chats';

  getChats() {
    return this.http.get<ChatItem[]>(this.chatsUrl);
  }

  getChatHistory(chatId: string, page = 0, size = 50) {
    return this.http.get<{ content: MessageItem[] }>(
      `${this.chatsUrl}/${chatId}/messages?page=${page}&size=${size}`
    );
  }

  createDirectChat(friendId: string) {
    return this.http.post<ChatItem>(`${this.chatsUrl}/create`, { user2: friendId });
  }

  sendFile(chatId: string, file: File) {
    const formData = new FormData();
    const messageReq = { content: file.name, type: 'FILE' };
    
    formData.append('message', new Blob([JSON.stringify(messageReq)], { type: 'application/json' }));
    formData.append('file', file);

    return this.http.post<MessageItem>(`${this.chatsUrl}/${chatId}/messages`, formData);
  }

  sendAudio(chatId: string, audioBlob: Blob, duration: number) {
    const formData = new FormData();
    formData.append('audio', new Blob([JSON.stringify({ duration })], { type: 'application/json' }));
    formData.append('file', audioBlob, 'audio-record.webm');

    return this.http.post<MessageItem>(`${this.chatsUrl}/${chatId}/messages/audio`, formData);
  }

  createGroupChat(name: string, memberIds: string[]) {
    return this.http.post<ChatItem>(`${this.chatsUrl}/group`, { name, memberIds });
  }

  getAttachmentUrl(chatId: string, messageId: string) {
    return this.http.get<{ url: string }>(`${this.chatsUrl}/${chatId}/messages/${messageId}/attachment-url`);
  }

  deleteChat(chatId: string) {
    return this.http.delete(`${this.chatsUrl}/${chatId}`);
  }
}
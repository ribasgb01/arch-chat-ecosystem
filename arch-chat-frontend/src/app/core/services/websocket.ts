import { Injectable } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;

  connect(token: string, onConnectCallback: () => void) {
    if (this.isConnected && this.client) {
      onConnectCallback();
      return;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    this.client = new Client({
      brokerURL: `${protocol}//${window.location.host}/ws/websocket`,
      connectHeaders: {
        Authorization: `Bearer ${token}`
      },
      debug: (str) => console.log('[STOMP Debug]:', str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000
    });

    this.client.onConnect = () => {
      this.isConnected = true;
      console.log('✅ Conectado ao WebSocket do Chat com sucesso!');
      onConnectCallback();
    };

    this.client.onStompError = (frame) => {
      console.error('❌ Erro no WebSocket STOMP:', frame);
    };

    this.client.activate();
  }

  subscribeToChat(chatId: string, callback: (message: any) => void): StompSubscription | null {
    if (!this.client || !this.isConnected) return null;

    const destination = `/exchange/chat.exchange/chat.message.${chatId}`;
    return this.client.subscribe(destination, (msg: IMessage) => {
      const payload = JSON.parse(msg.body);
      callback(payload);
    });
  }

  sendMessage(chatId: string, content: string) {
    if (!this.client || !this.isConnected) return;

    this.client.publish({
      destination: `/app/chat/${chatId}/sendMessage`,
      body: JSON.stringify({ content, type: 'TEXT' })
    });
  }

  editMessage(chatId: string, messageId: string, content: string) {
    if (!this.client || !this.isConnected) return;

    this.client.publish({
      destination: `/app/chat/${chatId}/editMessage`,
      body: JSON.stringify({ messageId, content })
    });
  }

  deleteMessage(chatId: string, messageId: string) {
    if (!this.client || !this.isConnected) return;

    this.client.publish({
      destination: `/app/chat/${chatId}/deleteMessage`,
      body: JSON.stringify(messageId)
    });
  }

  disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.isConnected = false;
    }
  }
}
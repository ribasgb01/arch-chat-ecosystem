import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface SystemNotification {
  id: string;
  senderId: string;
  receiverId: string;
  chatId: string | null;
  type: string;
  read: boolean;
  content: string;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private http = inject(HttpClient);
  private notifUrl = '/api/notifications';

  getNotifications(page = 0, size = 10) {
    return this.http.get<{ content: SystemNotification[] }>(
      `${this.notifUrl}?page=${page}&size=${size}`
    );
  }

  markAsRead(notificationId: string) {
    return this.http.patch(`${this.notifUrl}/${notificationId}/read`, {});
  }
}
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface UserSearch {
  id: string;
  username: string;
  nickname: string;
}

@Injectable({ providedIn: 'root' })
export class FriendshipService {
  private http = inject(HttpClient);
  
  private usersUrl = '/api/users';
  private friendshipsUrl = '/api/friendships';

  searchUsers(query: string) {
    return this.http.get<UserSearch[]>(`${this.usersUrl}/search?query=${query}`);
  }

  sendFriendRequest(receiverId: string) {
    return this.http.post(`${this.friendshipsUrl}/request`, { receiverId });
  }

  getPendingRequests() {
    return this.http.get<any[]>(`${this.friendshipsUrl}/pending`);
  }

  acceptRequest(friendshipId: string) {
    return this.http.patch(`${this.friendshipsUrl}/${friendshipId}/accept`, {});
  }

  getUserById(id: string) {
    return this.http.get<UserSearch>(`${this.usersUrl}/${id}`);
  }

  blockUser(blockedId: string) {
    return this.http.post(`${this.friendshipsUrl}/block`, { blockedId });
  }

  unblockUser(blockedId: string) {
    return this.http.post(`${this.friendshipsUrl}/unblock`, { blockedId });
  }

  declineRequest(friendshipId: string) {
    return this.http.patch(`${this.friendshipsUrl}/${friendshipId}/decline`, {});
  }

  getAcceptedFriends() {
    return this.http.get<string[]>(this.friendshipsUrl);
  }
}
import { Injectable, signal } from '@angular/core';
import { Client, StompSubscription } from '@stomp/stompjs';

@Injectable({ providedIn: 'root' })
export class CallService {
  private stompClient: Client | null = null;
  private peerConnection: RTCPeerConnection | null = null;
  private callSubscription: StompSubscription | null = null;

  callStatus = signal<'IDLE' | 'CALLING' | 'RINGING' | 'CONNECTED'>('IDLE');
  callerId = signal<string | null>(null);
  remoteUserId = signal<string | null>(null);

  localStream = signal<MediaStream | null>(null);
  remoteStream = signal<MediaStream | null>(null);

  private pendingOffer: any = null;
  private iceCandidatesQueue: any[] = [];

  private rtcConfig: RTCConfiguration = {
    iceServers: [
      { urls: 'stun:stun.l.google.com:19302' },
      { urls: 'stun:stun1.l.google.com:19302' }
    ]
  };

  connect(token: string, myUserId: string) {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    this.stompClient = new Client({
      brokerURL: `${protocol}//${window.location.host}/ws-call/websocket`,
      connectHeaders: { Authorization: `Bearer ${token}` },
      reconnectDelay: 5000
    });

    this.stompClient.onConnect = () => {
      console.log('✅ Conectado ao serviço de Calling (WebRTC)!');
      this.callSubscription = this.stompClient!.subscribe(`/topic/call.user.${myUserId}`, (msg) => {
        const signalData = JSON.parse(msg.body);
        this.handleSignal(signalData);
      });
    };

    this.stompClient.activate();
  }

  async startCall(targetUserId: string) {
    this.remoteUserId.set(targetUserId);
    this.callStatus.set('CALLING');

    await this.initLocalCamera();
    this.createPeerConnection();

    const offer = await this.peerConnection!.createOffer();
    await this.peerConnection!.setLocalDescription(offer);

    this.sendSignal('CALL_OFFER', offer);
  }

  async acceptCall() {
    this.callStatus.set('CONNECTED');
    await this.initLocalCamera();
    this.createPeerConnection();

    await this.peerConnection!.setRemoteDescription(new RTCSessionDescription(this.pendingOffer));

    const answer = await this.peerConnection!.createAnswer();
    await this.peerConnection!.setLocalDescription(answer);

    this.sendSignal('CALL_ANSWER', answer);
    this.drainIceCandidates();
  }

  hangUp() {
    if (this.remoteUserId()) {
      this.sendSignal('HANG_UP', null);
    }
    this.cleanup();
  }

  private async initLocalCamera() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      this.localStream.set(stream);
    } catch (e: any) {
      alert('Erro ao acessar câmera/microfone: ' + e.message);
    }
  }

  private createPeerConnection() {
    this.peerConnection = new RTCPeerConnection(this.rtcConfig);

    if (this.localStream()) {
      this.localStream()!.getTracks().forEach(track => {
        this.peerConnection!.addTrack(track, this.localStream()!);
      });
    }

    this.peerConnection.ontrack = (event) => {
      this.remoteStream.set(event.streams[0]);
    };

    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignal('ICE_CANDIDATE', event.candidate);
      }
    };
  }

  private async handleSignal(signalData: any) {
    const { type, senderId, data } = signalData;

    if (type === 'CALL_OFFER') {
      this.callerId.set(senderId);
      this.remoteUserId.set(senderId);
      this.pendingOffer = data;
      this.callStatus.set('RINGING'); // Telefone tocando!
    } 
    else if (type === 'CALL_ANSWER') {
      await this.peerConnection?.setRemoteDescription(new RTCSessionDescription(data));
      this.callStatus.set('CONNECTED');
      this.drainIceCandidates();
    } 
    else if (type === 'ICE_CANDIDATE') {
      const candidate = new RTCIceCandidate(data);
      if (this.peerConnection?.remoteDescription?.type) {
        await this.peerConnection.addIceCandidate(candidate);
      } else {
        this.iceCandidatesQueue.push(candidate);
      }
    } 
    else if (type === 'HANG_UP') {
      this.cleanup();
    }
  }

  private async drainIceCandidates() {
    while (this.iceCandidatesQueue.length > 0) {
      const candidate = this.iceCandidatesQueue.shift();
      try {
        await this.peerConnection?.addIceCandidate(candidate);
      } catch (e) {
        console.error('Erro ao adicionar candidato ICE:', e);
      }
    }
  }

  private sendSignal(type: string, data: any) {
    if (!this.stompClient || !this.remoteUserId()) return;

    this.stompClient.publish({
      destination: '/app/call/signal',
      body: JSON.stringify({
        receiverId: this.remoteUserId(),
        type: type,
        data: data
      })
    });
  }

  private cleanup() {
    this.callStatus.set('IDLE');
    if (this.localStream()) {
      this.localStream()!.getTracks().forEach(t => t.stop());
      this.localStream.set(null);
    }
    this.remoteStream.set(null);
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
    this.pendingOffer = null;
    this.remoteUserId.set(null);
    this.callerId.set(null);
    this.iceCandidatesQueue = [];
  }
}
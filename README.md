<div align="center">

# ⚡ ArchChat Platform

**Ecossistema distribuído de mensageria em tempo real, sinalização WebRTC P2P e arquitetura orientada a eventos orquestrado em Kubernetes.**

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](#)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](#)
[![Angular](https://img.shields.io/badge/Angular-18-DD0031?style=for-the-badge&logo=angular&logoColor=white)](#)
[![WebRTC](https://img.shields.io/badge/WebRTC-P2P_Signaling-333333?style=for-the-badge&logo=webrtc&logoColor=white)](#)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-STOMP_Relay-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)](#)
<br>
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](#)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](#)
[![Redis](https://img.shields.io/badge/Redis-7.0-DC382D?style=for-the-badge&logo=redis&logoColor=white)](#)
[![MinIO](https://img.shields.io/badge/MinIO-S3_Storage-C72C48?style=for-the-badge&logo=minio&logoColor=white)](#)
<br>
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ED?style=for-the-badge&logo=docker&logoColor=white)](#)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Orchestration-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)](#)
[![TailwindCSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)](#)

<p align="center">
  <a href="#-visão-geral">Visão Geral</a> •
  <a href="#-arquitetura--design-de-engenharia">Arquitetura</a> •
  <a href="#-matriz-de-persistência-poliglota">Persistência Poliglota</a> •
  <a href="#-microsserviços--responsabilidades">Microsserviços</a> •
  <a href="#-recursos-em-destaque">Recursos</a> •
  <a href="#-execução-local-com-kubernetes">Execução Local</a> •
  <a href="#-segurança--boas-práticas-de-produção">Segurança</a>
</p>

</div>

---

## 🎯 Visão Geral

O **ArchChat** é uma plataforma completa de comunicação em tempo real projetada sob os pilares da **Clean Architecture**, **Domain-Driven Design (DDD)** e **Microsserviços Reativos**. 

O sistema resolve problemas clássicos de concorrência e escalabilidade encontrados em mensagerias corporativas modernas:
* **Desacoplamento de I/O bloqueante:** upload de mídia e processamento assíncrono via eventos no **RabbitMQ**.
* **Chamadas de áudio e vídeo de latência ultrabaixa:** sinalização pura desacoplada usando STOMP sobre WebSockets e handshake P2P via **WebRTC**.
* **Proteção contra estouro de memória (OOM):** consultas de histórico particionadas via paginação com MongoDB e resolução tardia de mídias sob demanda no **MinIO (S3)**.
* **Segurança stateless com invalidação ativa:** autenticação JWT combinada com blacklist distribuída em **Redis**.

---

## 🏛️ Arquitetura & Design de Engenharia

O tráfego externo acessa o cluster via um **Ingress Controller** unificado, que elimina problemas de CORS ao rotear tanto o Single Page Application (SPA) quanto as chamadas de API e WebSockets no mesmo ponto de entrada.

```text
                                  [ CLIENTE (Navegador) ]
                                             │
                                             ▼
                             ┌───────────────────────────────┐
                             │    KUBERNETES INGRESS         │
                             │ (Roteador de Borda / SSL)     │
                             └───────┬───────────────┬───────┘
                     / (Assets)      │               │ /api/** & /ws/**
                     ┌───────────────┘               └───────────────┐
                     ▼                                               ▼
         ┌─────────────────────────┐                   ┌───────────────────────────┐
         │   arch-chat-frontend    │                   │   arch-chat-api-gateway   │
         │ (Angular 18 + Nginx)    │                   │ (Spring Cloud Gateway)    │
         └─────────────────────────┘                   └─────────────┬─────────────┘
                                                                     │
            ┌────────────────────────┬───────────────────────────────┼──────────────────────────────┐
            ▼                        ▼                               ▼                              ▼
┌───────────────────────┐┌───────────────────────┐       ┌───────────────────────┐      ┌───────────────────────┐
│       AUTH / USER     ││  CHAT / FRIENDSHIP    │       │        CALLING        │      │     NOTIFICATION      │
│   (Identity & Auth)   ││ (Mensagens & Salas)   │       │   (WebRTC Signaling)  │      │  (E-mail & In-App)    │
└───────────┬───────────┘└───────────┬───────────┘       └───────────┬───────────┘      └───────────┬───────────┘
            │                        │                               │                              │
            │   ┌────────────────────┴───────────────────────────────┴──────────────────────────────┤
            ▼   ▼                                                                                   ▼
    [ REDIS CLUSTER ]                                                                       [ RABBITMQ BROKER ]
 (Blacklist & Presença)                                                                  (Eventos & STOMP Relay)
```

### 💡 Decisões Arquiteturais de Destaque

* **Separação Roster vs. Conversação:**
  O ciclo de vida das amizades é estritamente isolado do ciclo de vida das conversas. Excluir uma sala de chat no MongoDB não corrompe o vínculo de amizade persistido no PostgreSQL.
* **Mitigação do Gargalo N+1 no MinIO:**
  Ao carregar o histórico de conversas, o backend não gera URLs pré-assinadas em massa. O cliente recebe o identificador do anexo e consome uma rota dedicada para emissão assíncrona da URL temporária (15 min) apenas quando o elemento visual entra na viewport.
* **Gerenciamento de Sessão Distribuída:**
  Para viabilizar múltiplos dispositivos simultâneos sem sobrescrita de sessões, os tokens de atualização são gerenciados com escopo por dispositivo/sessão e validados via Redis com TTL próprio.

---

## 🗄️ Matriz de Persistência Poliglota

Cada tecnologia de banco de dados foi selecionada rigorosamente pelo perfil de carga da sua regra de negócio:

| Tecnologia | Tipo | Caso de Uso no ArchChat |
| :--- | :--- | :--- |
| **PostgreSQL 16** | ACID Relacional | Contas de usuários, credenciais hash (BCrypt), vínculos de amizade e notificações persistidas. |
| **MongoDB 7.0** | Documento NoSQL | Mensagens de texto, dados estruturados de anexos e salas de chat diretas/grupos. |
| **Redis 7.0** | Chave-Valor em Memória | Blacklist de revogação de tokens JWT em tempo real e tracking de presença online via Sets de sessões WebSocket. |
| **MinIO** | Object Storage (API S3) | Armazenamento de alta performance para fotos, documentos e gravações de áudio (`.webm`). |
| **RabbitMQ 3.x** | AMQP + STOMP Broker | Barramento de eventos assíncronos e distribuição de mensagens em tempo real para múltiplos nós de chat. |

---

## 🧩 Microsserviços & Responsabilidades

```bash
arch-chat/
├── arch-chat-api-gateway/          # Roteamento dinâmico, filtros reativos e CORS
├── arch-chat-user-service/         # Cadastro, login, JWT, refresh e busca por nickname
├── arch-chat-messaging-service/    # Mensagens em tempo real, histórico, MinIO e amizades
├── arch-chat-calling-service/      # Troca de sinais WebRTC (SDP Offer/Answer e ICE)
├── arch-chat-notification-service/ # Consumidor RabbitMQ para e-mails e histórico de avisos
├── arch-chat-frontend/             # Interface Angular 18 (Signals, Tailwind, WebSockets)
└── k8s/                            # Manifestos declarativos do cluster Kubernetes
```

### 1. `arch-chat-api-gateway` (Porta 8080)
* Atua como Proxy Reverso central de entrada.
* Despacha requisições HTTP para os serviços adequados.
* Realiza o upgrade de protocolo e mantém túneis de WebSocket abertos para `/ws/**` e `/ws-call/**`.

### 2. `arch-chat-user-service` (Porta 8081)
* Cadastro com validação de idade mínima e unicidade de e-mail e nickname.
* Envio de código de verificação via barramento assíncrono (RabbitMQ).
* Emissão de par de tokens: Token de Acesso (curto) e Token de Atualização (Cookie HttpOnly).
* Endpoint de logout com inclusão imediata do token na blacklist do Redis.

### 3. `arch-chat-messaging-service` (Porta 8082)
* Controle de solicitações de amizade (enviar, aceitar, recusar, bloquear e desbloquear).
* Criação de chats individuais e em grupo com múltiplos participantes.
* Envio de mensagens em tempo real via STOMP sobre RabbitMQ Relay.
* Suporte nativo a edição de texto, exclusão lógica com substituição visual e anexo de mídias.
* Streaming de gravação de áudio do microfone para o MinIO.

### 4. `arch-chat-calling-service` (Porta 8084)
* Sinalização WebRTC 100% desacoplada da mensageria de texto.
* Roteamento de pacotes `CALL_OFFER`, `CALL_ANSWER`, `ICE_CANDIDATE` e `HANG_UP`.
* Fila interna no frontend para garantir que candidatos ICE recebidos antes da resposta remota não sejam descartados.

### 5. `arch-chat-notification-service` (Porta 8083)
* Consome eventos de sistema publicados na exchange `notification.exchange`.
* Disparo de e-mails formatados em HTML com Mailtrap/SMTP.
* Armazenamento e controle de leitura (`/read`) de alertas in-app com paginação.

### 6. `arch-chat-frontend` (Porta 80)
* Desenvolvido em Angular 18 com componentes autônomos (*Standalone Components*).
* Gerenciamento de estado com Angular Signals e RxJS.
* Interceptor HTTP funcional injetando credenciais Bearer e capturando expirações.
* Interface gráfica inspirada em temas escuros com Tailwind CSS.

---

## 🚀 Execução Local com Kubernetes

Toda a stack está configurada para subir em ambiente local através do **Minikube** (driver Docker) ou em clusters gerenciados (EKS, GKE, AKS).

### Pré-requisitos
* Docker instalado e ativo
* Minikube e `kubectl` configurados

### Passo a Passo

1. **Iniciar o Cluster & Habilitar Ingress:**
   ```bash
   minikube start --driver=docker
   minikube addons enable ingress
   ```

2. **Apontar o Terminal para o Daemon do Minikube:**
   *(Permite construir as imagens diretamente no registro interno do cluster)*
   ```bash
   eval $(minikube docker-env)
   ```

3. **Construir as Imagens dos Serviços (Multi-Stage):**
   ```bash
   docker build -t arch-chat-api-gateway:latest ./arch-chat-api-gateway
   docker build -t arch-chat-user-service:latest ./arch-chat-user-service
   docker build -t arch-chat-messaging-service:latest ./arch-chat-messaging-service
   docker build -t arch-chat-calling-service:latest ./arch-chat-calling-service
   docker build -t arch-chat-notification-service:latest ./arch-chat-notification-service
   docker build -t arch-chat-frontend:latest ./arch-chat-frontend
   ```

4. **Aplicar os Manifestos K8s:**
   ```bash
   kubectl apply -f k8s/
   ```

5. **Monitorar a Inicialização:**
   ```bash
   kubectl get pods -w
   ```
   *Aguarde todos os contêineres mudarem para o estado `Running (1/1)`.*

6. **Acessar a Plataforma:**
   Em uma aba dedicada do seu terminal, execute o túnel de rede:
   ```bash
   minikube tunnel
   ```
   Abra seu navegador em: 👉 `http://localhost`

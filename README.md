# Online Yut Game Project

## 🎮 Introduction
Java 소켓 프로그래밍을 활용한 멀티플레이어 온라인 윷놀이 게임입니다. 최대 4명이 동시에 게임을 즐길 수 있습니다.

## 🔍 Features
- 실시간 멀티플레이어 (최대 4인)
- 실시간 채팅 시스템
- 윷 던지기 및 말 이동 시스템
- 기권 시스템
- 게임 승패 판정 시스템

## 🛠 Tech Stack
- Java
- Socket Programming
- Swing (GUI)
- Object Stream

## 📝 Communication Protocol
| Code | Direction | Purpose | Format |
|------|-----------|---------|---------|
| 100 | Client → Server | Login request | username 100 Hello |
| 101 | Server → Client | User index allocation | SERVER 101 userIdx |
| 102 | Server → All | Update user info | SERVER 102 userIdx userName isOwner isReady |
| 103 | Client → Server | Ready status change | username 103 "" |
| 104 | Client → Server | Game start request | username 104 "" |
| 105 | Server → All | Game start response | SERVER 105 true/false reason |
| 200 | Bidirectional | Chat message | username 200 message |
| 300 | Bidirectional | Image transmission | username 300 imageData |
| 400 | Client → Server | Logout | username 400 "" |
| 500 | Server → All | Turn information | SERVER 500 username playTurnIdx true |
| 501 | Client → Server | Yut throw result | username 501 yutResult value |
| 502 | Server → All | Additional turn | SERVER 502 roll again |
| 503 | Server → All | Yut result list | SERVER 503 resultList |
| 504 | Client → Server | Piece movement | username 504 moveInfo |
| 505 | Server → All | Remaining pieces | SERVER 505 restPieceCounts |
| 506 | Client → Server | Surrender | username 506 "" |
| 999 | Server → Client | Room full notification | SERVER 999 Room is full |

## 🚀 Getting Started

### Prerequisites
- Java Runtime Environment (JRE)
- Network connection

### Installation & Running
1. Server Start
```bash
java YutGameServer
```

2. Client Start
```bash
java JavaGameClientMain
```

## 🎯 Game Rules
1. 2-4명의 플레이어가 참여 가능
2. 모든 플레이어가 준비 상태가 되면 게임 시작
3. 순서대로 윷을 던지고 말을 이동
4. 모든 말을 도착점에 보내면 승리
5. 상대방 말을 잡으면 추가 턴 획득

## 📁 Project Structure
```
src/
├── client/
│   ├── JavaGameClientMain.java
│   └── YutGameClientView.java
├── server/
│   ├── YutGameServer.java
│   └── UserService.java
└── common/
    └── ChatMsg.java
```

## 🔧 Exception Handling
- Network Disconnections
- Abnormal Client Termination
- Player Abandonment
- Data Transfer Errors

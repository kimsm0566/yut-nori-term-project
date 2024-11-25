package Yootgame.source.server.multiroom;

import java.util.*;
import java.util.concurrent.*;

// 방 생성, 삭제 및 관리 클래스
public class RoomManager {

    // 방 이름과 클라이언트 목록을 맵핑하고 관리함
    // rooms 는 Map 인터페이스 타입을 가짐 키(String) 와 값 Set<ClientHandler> 의 쌍으로 구성됨
    // 키 는 방의 이름 , 값은 해당 방에 속한 클라이언트 의 집합을 나타냄

    /* ConcurrentHashMap<>() 은 스레드 안전(thread-safe)한 Map 구현체
    내부에서 동기화 제공하여 여러 스레드가 동시에 이 맵을 수정하거나 읽을 수 있도록 함
    https://parkmuhyeun.github.io/woowacourse/2023-09-09-Concurrent-Hashmap/ << 참고함
    */
    private final Map< String, Set<ClientHandler> > rooms = new ConcurrentHashMap<>();

    // 룸 생성
    public synchronized void createRoom(String roomName) {
        rooms.putIfAbsent(roomName, ConcurrentHashMap.newKeySet());
        // 턴 시간
        // 말 개수
        // putIfAnsent: Key 값이 존재하는 경우 Map의 Value의 값을 반환하고, Key값이 존재하지 않는 경우
        // key와 Value를 Map에 저장하고 Null을 반환함
    }

    // 룸 입장
    public synchronized boolean joinRoom(String roomName, ClientHandler client) {
        Set<ClientHandler> room = rooms.get(roomName); // ClientHandler 에 있는 roomName을 가져옴
        if (room == null) return false; // 방이 없으면 실패
        room.add(client); //클라이언트를 방에 추가
        return true;
    }

    // 룸 목록 조회
    public synchronized List<String> listRooms() {
        return new ArrayList<>(rooms.keySet());
        // 턴 시간
        // 말 개수
    }

    // 룸 퇴장
    public synchronized void leaveRoom(String roomName, ClientHandler client) {
        Set<ClientHandler> room = rooms.get(roomName);
        if (room != null) {
            room.remove(client); //클라이언트를 방에서 제거
            if (room.isEmpty()) {
                rooms.remove(roomName); // 방에 클라이언트가 없으면 룸 삭제
            }
        }
    }
}



package com.example.departorium.socket;

import com.example.departorium.entity.ProjectEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ToString
@Getter
public class ChatRoom {
    String id;
    String name;
    ProjectEntity project;

    // 세션을 동기화된 세션 리스트로 관리 (CopyOnWriteArraySet 사용)
    Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Builder
    public ChatRoom(String room_id, String room_name, ProjectEntity room_project) {
        this.id = room_id;
        this.name = room_name;
        this.project = room_project;
    }

    /**
     * 메시지를 처리하는 메서드.
     * JOIN 메시지일 경우 방에 참가하고, 그 외의 메시지일 경우 전송 처리
     */
    public void handleMessage(WebSocketSession session, ChatMessage chatMessage, ObjectMapper objectMapper) throws JsonProcessingException {
        if (chatMessage.getType().equals("JOIN")) {
            join(session);
        } else {
            send(chatMessage, objectMapper);
        }
    }

    /**
     * 세션을 방에 추가하는 메서드
     */
    private void join(WebSocketSession session) {
        sessions.add(session);
    }

    /**
     * 메시지를 방에 있는 모든 클라이언트로 전송하는 메서드
     * 세션이 열려있는지 확인 후 전송
     */
    private <T> void send(T messageObject, ObjectMapper objectMapper) throws JsonProcessingException {
        TextMessage message = new TextMessage(objectMapper.writeValueAsString(messageObject));
        // 세션이 열린 경우에만 메시지 전송
        sessions.parallelStream().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                } else {
                    // 세션이 닫혀있으면 세션 목록에서 제거
                    sessions.remove(session);
                    System.out.println("Session closed, removed from the session list: " + session.getId());
                }
            } catch (IOException e) {
                // 예외 발생 시 에러 로그 출력
                e.printStackTrace();
                System.out.println("Error sending message to session: " + session.getId());
            }
        });
    }

    /**
     * 특정 세션을 방에서 제거하는 메서드
     */
    public void remove(WebSocketSession target) {
        sessions.remove(target);
        System.out.println("Session removed: " + target.getId());
    }

    /**
     * 현재 방에 연결된 모든 세션을 반환
     */
    private Set<WebSocketSession> getSessions() {
        return sessions;
    }
}

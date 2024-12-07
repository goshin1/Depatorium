package com.example.departorium.socket;

import com.example.departorium.entity.DepartEntity;
import com.example.departorium.entity.ProjectEntity;
import com.example.departorium.repository.DepartRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ObjectMapper objectMapper;
    private Map<String, ChatRoom> chatRooms;

    @Autowired
    private DepartRepository departRepository;

    @PostConstruct
    private void init(){
        chatRooms = new LinkedHashMap<>();
        List<DepartEntity> roomEntityList = departRepository.findAll();
        for(int i = 0; i < roomEntityList.size(); i++){
            DepartEntity roomEntity = roomEntityList.get(i);
            ChatRoom chatRoom = ChatRoom.builder()
                    .room_id(roomEntity.getToken())
                    .room_name(roomEntity.getSubtitle())
                    .room_project(roomEntity.getProject())
                    .build();
            chatRooms.put(roomEntity.getToken(), chatRoom);
        }

    }

    public Collection<ChatRoom> findAllRoom(){
        return  chatRooms.values();
    }

    public ChatRoom findRoomById(String roomId){
        return chatRooms.get(roomId);
    }

    public ChatRoom createRoom(ProjectEntity project, String name){
        String randomId = UUID.randomUUID().toString();
        ChatRoom chatRoom = ChatRoom.builder()
                .room_id(randomId)
                .room_name(name)
                .room_project(project)
                .build();
        chatRooms.put(randomId, chatRoom);
        return chatRoom;
    }

}

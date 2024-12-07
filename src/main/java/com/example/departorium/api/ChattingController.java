package com.example.departorium.api;


import com.example.departorium.dto.ChatDTO;

import com.example.departorium.entity.ChatEntity;
import com.example.departorium.service.ChattingService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;


@RestController
public class ChattingController {

    private final ChattingService chatService;

    public ChattingController(ChattingService chatSave) {
        this.chatService = chatSave;
    }

    // 입력된 채팅 내용 저장.
    @PostMapping("/project/{project_id}/{depart_id}/chat/save")
    public ResponseEntity<ChatEntity> save(@CookieValue("refresh") String refresh,
                                           @PathVariable Long project_id,
                                           @PathVariable Long depart_id,
                                           @RequestBody ChatDTO chatDTO){

        System.out.println("=== chat ===");
        ChatEntity saved = chatService.saveProcess(refresh, project_id, depart_id, chatDTO);
        System.out.println("=== res ===");
        System.out.println(saved.toString());
        return (saved != null) ?
                ResponseEntity.status(HttpStatus.OK).body(saved) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 이전 채팅 내역 목록.
    @GetMapping("/project/{project_id}/{depart_id}/chat/list")
    public ResponseEntity<List<ChatEntity>> list(@PathVariable Long depart_id) {
        List<ChatEntity> listed = chatService.listProcess(depart_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }



}
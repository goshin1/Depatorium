package com.example.departorium.api;


import com.example.departorium.dto.CommentDTO;
import com.example.departorium.entity.CommentEntity;
import com.example.departorium.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 생성.
    @PostMapping("/comment/create/{project_id}/{schedule_id}")
    public ResponseEntity<CommentEntity> create(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long schedule_id, @RequestBody CommentDTO commentDTO) {
        CommentEntity created = commentService.createProcess(refresh, project_id, schedule_id, commentDTO);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 수정.
    @PatchMapping("/comment/update/{project_id}/{comment_id}")
    public ResponseEntity<CommentEntity> update(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long comment_id, @RequestBody CommentDTO commentDTO) {
        CommentEntity updated = commentService.updateProcess(refresh, project_id, comment_id, commentDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 삭제.
    @DeleteMapping("/comment/delete/{project_id}/{comment_id}")
    public ResponseEntity<CommentEntity> delete(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long comment_id) {
        CommentEntity deleted = commentService.deleteProcess(refresh, project_id, comment_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 목록.
    @GetMapping("/comment/list/{project_id}/{schedule_id}")
    public ResponseEntity<List<CommentEntity>> list(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long schedule_id) {
        List<CommentEntity> listed = commentService.listProcess(refresh, project_id, schedule_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

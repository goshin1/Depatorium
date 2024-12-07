package com.example.departorium.api;

import com.example.departorium.dto.CommentDTO;
import com.example.departorium.dto.QuestionDTO;
import com.example.departorium.entity.QuestionEntity;
import com.example.departorium.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QuestionController {
    public final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    // 질문 생성.
    @PostMapping("/question/create/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<QuestionEntity> create(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id, @RequestBody QuestionDTO questionDTO) {
        QuestionEntity created = questionService.createProcess(refresh, project_id, depart_id, task_id, questionDTO);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 질문 수정.
    @PatchMapping("/question/update/{project_id}/{depart_id}/{question_id}")
    public ResponseEntity<QuestionEntity> update(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long question_id, @RequestBody QuestionDTO questionDTO) {
        QuestionEntity updated = questionService.updateProcess(refresh, project_id, depart_id, question_id, questionDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 질문 삭제.
    @DeleteMapping("/question/delete/{project_id}/{depart_id}/{question_id}")
    public ResponseEntity<QuestionEntity> delete(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long question_id) {
        QuestionEntity deleted = questionService.deleteProcess(refresh, project_id, depart_id, question_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 댓글 목록.
    @GetMapping("/question/list/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<List<QuestionEntity>> list(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id) {
        List<QuestionEntity> listed = questionService.listProcess(refresh, project_id, depart_id, task_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

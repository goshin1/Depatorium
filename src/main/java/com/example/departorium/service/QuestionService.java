package com.example.departorium.service;

import com.example.departorium.dto.*;
import com.example.departorium.entity.*;
import com.example.departorium.repository.*;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class QuestionService {
    private final UserService userService;
    private final TaskRepository taskRepository;
    private final QuestionRepository questionRepository;

    public QuestionService(UserService userService, TaskRepository taskRepository, QuestionRepository questionRepository) {
        this.userService = userService;
        this.taskRepository = taskRepository;
        this.questionRepository = questionRepository;
    }

    // 질문 생성.
    public QuestionEntity createProcess(String refresh, Long project_id, Long depart_id, Long task_id, QuestionDTO questionDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        TaskEntity isTask = taskRepository.findById(task_id).orElse(null);
        log.info(isTask.toString());

        String content = questionDTO.getContent();
        log.info(questionDTO.toString());

        QuestionEntity isQuestion = new QuestionEntity();
        isQuestion.setContent(content);
        isQuestion.setCreate(LocalDateTime.now());
        isQuestion.setUpdate(LocalDateTime.now());
        isQuestion.setMember(isMember);
        isQuestion.setTask(isTask);
        QuestionEntity saved = questionRepository.save(isQuestion);
        log.info(saved.toString());

        return saved;
    }

    // 질문 수정.
    public QuestionEntity updateProcess(String refresh, Long project_id, Long depart_id, Long question_id, QuestionDTO questionDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        QuestionEntity oldQuestion = questionRepository.findById(question_id).orElse(null);
        log.info(oldQuestion.toString());

        String content = questionDTO.getContent();
        log.info(questionDTO.toString());

        QuestionEntity newQuestion = new QuestionEntity();
        newQuestion.setContent(content);
        newQuestion.setUpdate(LocalDateTime.now());
        log.info(newQuestion.toString());

        oldQuestion.patch(newQuestion);
        QuestionEntity updated = questionRepository.save(oldQuestion);
        log.info(updated.toString());

        return updated;
    }

    // 질문 삭제.
    @Transactional
    public QuestionEntity deleteProcess(String refresh, Long project_id, Long depart_id, Long question_id) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        QuestionEntity deleteQuestion = questionRepository.findById(question_id).orElse(null);
        log.info(deleteQuestion.toString());

        questionRepository.delete(deleteQuestion);

        return deleteQuestion;
    }

    // 질문 목록.
    public List<QuestionEntity> listProcess(String refresh, Long project_id, Long depart_id, Long task_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        List<QuestionEntity> questionList = questionRepository.findAllByTask_IdOrderByCreate(task_id);
        log.info(questionList.toString());

        return questionList;
    }
}

package com.example.departorium.service;


import com.example.departorium.dto.CommentDTO;
import com.example.departorium.entity.CommentEntity;
import com.example.departorium.entity.MemberEntity;
import com.example.departorium.entity.ScheduleEntity;
import com.example.departorium.repository.CommentRepository;
import com.example.departorium.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentService {
    private final UserService userService;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public CommentService(UserService userService, ScheduleRepository scheduleRepository, CommentRepository commentRepository) {
        this.userService = userService;
        this.scheduleRepository = scheduleRepository;
        this.commentRepository = commentRepository;
    }

    // 댓글 생성.
    public CommentEntity createProcess(String refresh, Long project_id, Long schedule_id, CommentDTO commentDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (isMember.getStatus().equals("INACTIVE")) {
            return null;
        }

        ScheduleEntity isSchedule = scheduleRepository.findById(schedule_id).orElse(null);
        log.info(isSchedule.toString());

        String content = commentDTO.getContent();
        log.info(commentDTO.toString());

        CommentEntity isComment = new CommentEntity();
        isComment.setContent(content);
        isComment.setCreate(LocalDateTime.now());
        isComment.setUpdate(LocalDateTime.now());
        isComment.setUser(isMember.getUser());
        isComment.setProject(isMember.getProject());
        isComment.setSchedule(isSchedule);
        CommentEntity saved = commentRepository.save(isComment);
        log.info(saved.toString());

        return saved;
    }

    // 댓글 수정.
    public CommentEntity updateProcess(String refresh, Long project_id, Long comment_id, CommentDTO commentDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (isMember.getStatus().equals("INACTIVE")) {
            return null;
        }

        CommentEntity oldComment = commentRepository.findById(comment_id).orElse(null);
        log.info(oldComment.toString());

        String content = commentDTO.getContent();
        log.info(content.toString());

        CommentEntity newComment = new CommentEntity();
        newComment.setContent(content);
        newComment.setUpdate(LocalDateTime.now());
        log.info(newComment.toString());

        oldComment.patch(newComment);
        CommentEntity updated = commentRepository.save(oldComment);
        log.info(updated.toString());

        return updated;
    }

    // 댓글 삭제.
    @Transactional
    public CommentEntity deleteProcess(String refresh, Long project_id, Long comment_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (isMember.getStatus().equals("INACTIVE")) {
            return null;
        }

        CommentEntity deleteComment = commentRepository.findById(comment_id).orElse(null);
        log.info(deleteComment.toString());

        commentRepository.delete(deleteComment);

        return deleteComment;
    }

    // 댓글 목록.
    public List<CommentEntity> listProcess(String refresh, Long project_id, Long schedule_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (isMember.getStatus().equals("INACTIVE")) {
            return null;
        }

        List<CommentEntity> commentList = commentRepository.findAllByProject_IdAndSchedule_IdOrderByCreate(project_id, schedule_id);
        log.info(commentList.toString());

        return commentList;
    }
}

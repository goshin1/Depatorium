package com.example.departorium.service;


import com.example.departorium.dto.ScheduleDTO;
import com.example.departorium.entity.MemberEntity;
import com.example.departorium.entity.ProjectEntity;
import com.example.departorium.entity.ScheduleEntity;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.repository.CommentRepository;
import com.example.departorium.repository.MemberRepository;
import com.example.departorium.repository.ProjectRepository;
import com.example.departorium.repository.ScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScheduleService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public ScheduleService(ScheduleRepository scheduleRepository, ProjectRepository projectRepository, MemberRepository memberRepository, CommentRepository commentRepository, UserService userService) {
        this.scheduleRepository = scheduleRepository;
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

    // 일정 생성.
    public ScheduleEntity createProcess(String refresh, Long project_id, ScheduleDTO scheduleDTO) {
        UserEntity isUser = null;
        MemberEntity isMember = null;

        if (project_id == null) {
            isUser = userService.userProcess(refresh);
        } else {
            isMember = userService.memberProcess(refresh, project_id);

            if (!isMember.getRole().equals("LEADER")) {
                return null;
            }
        }

        String title = scheduleDTO.getTitle();
        String detail = scheduleDTO.getDetail();
        LocalDateTime start = scheduleDTO.getStart();
        LocalDateTime end = scheduleDTO.getEnd();
        log.info(scheduleDTO.toString());

        ScheduleEntity isSchedule = new ScheduleEntity();
        isSchedule.setTitle(title);
        isSchedule.setDetail(detail);
        isSchedule.setStart(start);
        isSchedule.setEnd(end);
        isSchedule.setCreate(LocalDateTime.now());
        isSchedule.setUpdate(LocalDateTime.now());

        if (project_id == null) {
            isSchedule.setUser(isUser);
            isSchedule.setProject(null);
        } else {
            isSchedule.setUser(isMember.getUser());
            isSchedule.setProject(isMember.getProject());
        }

        ScheduleEntity saved = scheduleRepository.save(isSchedule);
        log.info(saved.toString());

        return saved;
    }

    // 일정 수정.
    public ScheduleEntity updateProcess(String refresh, Long schedule_id, ScheduleDTO scheduleDTO) {
        UserEntity isUser = userService.userProcess(refresh);

        ScheduleEntity oldSchedule = scheduleRepository.findById(schedule_id).orElse(null);
        log.info(oldSchedule.toString());

        if (!oldSchedule.getUser().getId().equals(isUser.getId())) {
            return null;
        }

        String newTitle = scheduleDTO.getTitle();
        String newDetail = scheduleDTO.getDetail();
        LocalDateTime newStart = scheduleDTO.getStart();
        LocalDateTime newEnd = scheduleDTO.getEnd();
        log.info(scheduleDTO.toString());

        ScheduleEntity newSchedule = new ScheduleEntity();
        newSchedule.setTitle(newTitle);
        newSchedule.setDetail(newDetail);
        newSchedule.setStart(newStart);
        newSchedule.setEnd(newEnd);
        newSchedule.setUpdate(LocalDateTime.now());
        log.info(newSchedule.toString());

        oldSchedule.patch(newSchedule);
        ScheduleEntity updated = scheduleRepository.save(oldSchedule);
        log.info(updated.toString());

        return updated;
    }

    // 일정 삭제.
    @Transactional
    public ScheduleEntity deleteProcess(String refresh, Long schedule_id) {
        UserEntity isUser = userService.userProcess(refresh);

        ScheduleEntity deleteSchedule = scheduleRepository.findById(schedule_id).orElse(null);
        log.info(deleteSchedule.toString());

        if (!deleteSchedule.getUser().getId().equals(isUser.getId())) {
            return null;
        }

        // 일정의 댓글 삭제.
        commentRepository.deleteAllBySchedule_Id(deleteSchedule.getId());

        // 일정 삭제.
        scheduleRepository.delete(deleteSchedule);

        return deleteSchedule;
    }

    // 일정 접근.
    public ScheduleEntity viewProcess(String refresh, Long schedule_id) {
        userService.userProcess(refresh);

        ScheduleEntity viewSchedule = scheduleRepository.findById(schedule_id).orElse(null);
        log.info(viewSchedule.toString());


        return viewSchedule;
    }

    // 메인 페이지의 개인 일정 목록.
    public List<ScheduleEntity> personalListProcess(String refresh) {
        UserEntity isUser = userService.userProcess(refresh);

        List<ScheduleEntity> scheduleList = scheduleRepository.findAllByUser_Id(isUser.getId());
        log.info(scheduleList.toString());

        return scheduleList;
    }

    // 프로젝트 페이지의 프로젝트 일정 목록.
    public List<ScheduleEntity> projectListProcess(String refresh, Long project_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());

        if (!isMember.getProject().getId().equals(isProject.getId())) {
            return null;
        }

        List<ScheduleEntity> scheduleList = scheduleRepository.findAllByProject_Id(project_id);
        log.info(scheduleList.toString());

        return scheduleList;
    }

    // 프로젝트 페이지의 멤버 일정 목록.
    public List<ScheduleEntity> memberScheduleListProcess(String refresh, Long project_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());

        if (!isMember.getProject().getId().equals(isProject.getId())) {
            return null;
        }

        List<MemberEntity> memberList = memberRepository.findAllByProject_Id(project_id);
        log.info(memberList.toString());

        List<List<ScheduleEntity>> scheduleList = memberList.stream()
                .map(memberEntity -> scheduleRepository.findAllByUser_IdAndProject_Id(memberEntity.getUser().getId(), null))
                .toList();

        // 중첩 List 평탄화.
        List<ScheduleEntity> flatScheduleList = new ArrayList<>();
        scheduleList.forEach(flatScheduleList::addAll);
        log.info(flatScheduleList.toString());

        return flatScheduleList;
    }
}
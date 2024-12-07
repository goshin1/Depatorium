package com.example.departorium.service;



import com.example.departorium.dto.*;
import com.example.departorium.entity.*;
import com.example.departorium.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProjectService {
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final DepartRepository departRepository;
    private final MemberRepository memberRepository;
    private final ChatRepository chattingRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final QuestionRepository questionRepository;

    public ProjectService(UserService userService, ProjectRepository projectRepository, DepartRepository departRepository, MemberRepository memberRepository, ChatRepository chattingRepository, ScheduleRepository scheduleRepository, CommentRepository commentRepository, TaskRepository taskRepository, QuestionRepository questionRepository) {
        this.userService = userService;
        this.projectRepository = projectRepository;
        this.departRepository = departRepository;
        this.memberRepository = memberRepository;
        this.chattingRepository = chattingRepository;
        this.scheduleRepository = scheduleRepository;
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.questionRepository = questionRepository;
    }

    // 프로젝트 생성.
    public ProjectEntity createProcess(String refresh, ProjectDTO projectDTO) {
        UserEntity isUser = userService .userProcess(refresh);

        String title = projectDTO.getTitle();
        String comment = projectDTO.getComment();
        String invitation = projectDTO.getInvitation();
        log.info(projectDTO.toString());

        Boolean existsInvitation = projectRepository.existsByInvitation(invitation);

        // 동일한 초대코드가 존재하면,
        if (existsInvitation) {
            return null;
        }

        ProjectEntity isProject = new ProjectEntity();
        isProject.setTitle(title);
        isProject.setComment(comment);
        isProject.setInvitation(invitation);
        isProject.setStatus("ACTIVE");
        ProjectEntity projectSaved = projectRepository.save(isProject);
        log.info(projectSaved.toString());

        // 프로젝트의 대표 설정.
        MemberEntity isMember = new MemberEntity();
        isMember.setRole("LEADER");
        isMember.setStatus("ACTIVE");
        isMember.setDepart(null);
        isMember.setUser(isUser);
        isMember.setProject(projectSaved);
        MemberEntity memberSaved = memberRepository.save(isMember);
        log.info(memberSaved.toString());

        return projectSaved;
    }

    // 프로젝트 접근.
    public ProjectEntity viewProcess(String refresh, Long project_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        ProjectEntity viewProject = projectRepository.findById(project_id).orElse(null);
        log.info(viewProject.toString());

        return viewProject;
    }

    // 프로젝트 수정.
    public ProjectEntity updateProcess(String refresh, Long project_id, ProjectDTO projectDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        // 프로젝트 대표 확인
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        ProjectEntity oldProject = projectRepository.findById(project_id).orElse(null);
        log.info(oldProject.toString());

        String newTitle = projectDTO.getTitle();
        String newComment = projectDTO.getComment();
        log.info(projectDTO.toString());

        ProjectEntity newProject = new ProjectEntity();
        newProject.setTitle(newTitle);
        newProject.setComment(newComment);
        log.info(newProject.toString());

        oldProject.patch(newProject);
        ProjectEntity updated = projectRepository.save(oldProject);
        log.info(updated.toString());

        return updated;
    }

    // 프로젝트 삭제.
    @Transactional
    public ProjectEntity deleteProcess(String refresh, Long project_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        // 프로젝트 대표 확인
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        // 삭제할 프로젝트.
        ProjectEntity deleteProject = projectRepository.findById(project_id).orElse(null);
        log.info(deleteProject.toString());

        // 삭제할 프로젝트의 직무회의실 목록.
        List<DepartEntity> departList = departRepository.findAllByProject_Id(deleteProject.getId());
        log.info(departList.toString());

        // 삭제할 프로젝트의 업무 목록.
        List<TaskEntity> taskList = new ArrayList<>();
        for (DepartEntity depart : departList) {
            taskList = taskRepository.findAllByDepart_Id(depart.getId());
        }

        // 프로젝트에서 생성한 업무 질문 삭제.
        for (TaskEntity task : taskList) {
            questionRepository.deleteAllByTask_Id(task.getId());
        }
        // 프로젝트에서 생성한 업무 삭제.
        for (DepartEntity depart : departList) {
            taskRepository.deleteAllByDepart_Id(depart.getId());
        }
        // 프로젝트에서 생성한 일정 댓글 삭제.
        commentRepository.deleteAllByProject_Id(deleteProject.getId());
        // 프로젝트에서 생성한 일정 삭제.
        scheduleRepository.deleteAllByProject_Id(deleteProject.getId());
        // 프로젝트의 직무회의실에서 생성한 채팅 내역 삭제.
        for (DepartEntity depart : departList)
            chattingRepository.deleteAllByDepart_Id(depart.getId());
        // 프로젝트의 멤버 삭제.
        memberRepository.deleteByProject_Id(deleteProject.getId());
        // 프로젝트의 직무회의실 삭제.
        departRepository.deleteByProject_Id(deleteProject.getId());
        // 프로젝트 삭제.
        projectRepository.deleteById(deleteProject.getId());

        return deleteProject;
    }

    // 각 유저의 프로젝트 목록.
    public List<ProjectEntity> userProjectListProcess(String refresh) {
        UserEntity isUser = userService.userProcess(refresh);
        log.info(isUser.toString());

        List<MemberEntity> memberList = memberRepository.findAllByUser_Id(isUser.getId());
        log.info(memberList.toString());

        List<ProjectEntity> projectList = memberList.stream()
                .map(memberEntity -> projectRepository.findById(memberEntity.getProject().getId()).orElse(null))
                .toList();
        log.info(projectList.toString());

        return projectList;
    }
}
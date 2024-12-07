package com.example.departorium.service;


import com.example.departorium.dto.DepartDTO;
import com.example.departorium.entity.DepartEntity;
import com.example.departorium.entity.MemberEntity;
import com.example.departorium.entity.ProjectEntity;
import com.example.departorium.repository.ChatRepository;
import com.example.departorium.repository.DepartRepository;
import com.example.departorium.repository.ProjectRepository;
import com.example.departorium.socket.ChatRoom;
import com.example.departorium.socket.ChatService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DepartService {
    private final UserService userService;
    private final ProjectRepository projectRepository;
    private final DepartRepository departRepository;

    private final ChatService chatService;

    private final ChatRepository chatRepository;

    public DepartService(UserService userService, ProjectRepository projectRepository, DepartRepository departRepository, ChatService chatService, ChatRepository chatRepository) {
        this.userService = userService;
        this.projectRepository = projectRepository;
        this.departRepository = departRepository;
        this.chatService = chatService;
        this.chatRepository = chatRepository;
    }

    // 직무회의실 생성.
    public DepartEntity createProcess(String refresh, Long project_id, DepartDTO departDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        // 프로젝트의 대표 확인.
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());

        String subtitle = departDTO.getSubtitle();
        log.info(departDTO.toString());

        // 소켓 서버에 방 추가
        ChatRoom chatRoom = chatService.createRoom(isProject, subtitle);

        DepartEntity isDepart = new DepartEntity();
        isDepart.setSubtitle(subtitle);
        isDepart.setToken(chatRoom.getId());
        isDepart.setProject(isProject);
        DepartEntity saved = departRepository.save(isDepart);
        log.info(saved.toString());

        return saved;
    }

    // 직무회의실 접근.
    public DepartEntity viewProcess(String refresh, Long project_id, Long depart_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        DepartEntity viewDepart = departRepository.findById(depart_id).orElse(null);
        log.info(viewDepart.toString());

        return viewDepart;
    }

    // 직무회의실 수정.
    public DepartEntity updateProcess(String refresh, Long project_id, Long depart_id, DepartDTO departDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        // 프로젝트의 대표 확인.
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        DepartEntity oldDepart = departRepository.findById(depart_id).orElse(null);
        log.info(oldDepart.toString());

        String newSubtitle = departDTO.getSubtitle();

        DepartEntity newDepart = new DepartEntity();
        newDepart.setSubtitle(newSubtitle);
        log.info(newDepart.toString());

        oldDepart.patch(newDepart);
        DepartEntity updated = departRepository.save(oldDepart);
        log.info(updated.toString());

        return updated;
    }

    // 직무회의실 삭제.
    @Transactional
    public DepartEntity deleteProcess(String refresh, Long project_id, Long depart_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        // 프로젝트의 대표 확인.
        if (!isMember.getRole().equals("LEADER")) {
            return null;
        }

        DepartEntity deleteDepart = departRepository.findById(depart_id).orElse(null);
        log.info(deleteDepart.toString());

        // 직무회의실 채팅 데이터 삭제
        chatRepository.deleteAllByDepart_Id(depart_id);
        // 직무회의실 삭제.
        departRepository.deleteById(depart_id);

        return deleteDepart;
    }

    // 각 프로젝트의 직무회의실 목록.
    public List<DepartEntity> listProcess(Long project_id) {
        ProjectEntity isProject = projectRepository.findById(project_id).orElse(null);
        log.info(isProject.toString());

        List<DepartEntity> departList = departRepository.findAllByProject_Id(isProject.getId());
        log.info(departList.toString());

        return departList;
    }

    public String approveDepart(String refresh, Long project_id, Long depart_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);
        log.info(isMember.toString());

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return "null";
        }

        DepartEntity viewDepart = departRepository.findById(depart_id).orElse(null);
        log.info(viewDepart.toString());

        return "ok";
    }
}

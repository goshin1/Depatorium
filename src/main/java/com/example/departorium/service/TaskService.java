package com.example.departorium.service;

import com.example.departorium.dto.TaskDTO;
import com.example.departorium.entity.DepartEntity;
import com.example.departorium.entity.DistributionEntity;
import com.example.departorium.entity.MemberEntity;
import com.example.departorium.entity.TaskEntity;
import com.example.departorium.repository.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TaskService {
    private final UserService userService;
    private final DepartRepository departRepository;
    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;


    private final QuestionRepository questionRepository;
    private final DistributionRepository distributionRepository;

    public TaskService(UserService userService, DepartRepository departRepository, MemberRepository memberRepository, TaskRepository taskRepository, QuestionRepository questionRepository, DistributionRepository distributionRepository) {
        this.userService = userService;
        this.departRepository = departRepository;
        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
        this.questionRepository = questionRepository;
        this.distributionRepository = distributionRepository;
    }

    // 업무 생성.
    public TaskEntity createProcess(String refresh, Long project_id, Long depart_id, TaskDTO taskDTO) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (!isMember.getDepart().getId().equals(depart_id) || !isMember.getRole().equals("MANAGER")) {
            return null;
        }

        DepartEntity isDepart = departRepository.findById(depart_id).orElse(null);
        log.info(isDepart.toString());

        String title = taskDTO.getTitle();
        String description = taskDTO.getDescription();
        LocalDateTime start = taskDTO.getStart();
        LocalDateTime end = taskDTO.getEnd();
        String file = taskDTO.getFile();
        String related = taskDTO.getRelated();
        String distribution = taskDTO.getDistribution();
        log.info(taskDTO.toString());

        // 업무 저장.
        TaskEntity isTask = new TaskEntity();
        isTask.setTitle(title);
        isTask.setDescription(description);
        isTask.setStart(start);
        isTask.setEnd(end);
        isTask.setCreate(LocalDateTime.now());
        isTask.setUpdate(LocalDateTime.now());
        isTask.setFile(file);
        isTask.setRelated(related);
        isTask.setDepart(isDepart);
        isTask.setMember(isMember);
        isTask.setWork("");
        TaskEntity taskSaved = taskRepository.save(isTask);
        log.info(taskSaved.toString());

        // 업무 배정.
        if (distribution != null) {
            String[] distributionString = distribution.split(",");

            for (String member_id : distributionString) {
                MemberEntity distributionMember = memberRepository.findById(Long.parseLong(member_id)).orElse(null);
                log.info(distributionMember.toString());

                DistributionEntity isDistribution = new DistributionEntity();
                isDistribution.setMember(distributionMember);
                isDistribution.setTask(isTask);
                DistributionEntity distributionSaved = distributionRepository.save(isDistribution);
                log.info(distributionSaved.toString());
            }
        }

        return taskSaved;
    }

    // 업무 접근.
    public TaskEntity viewProcess(String refresh, Long project_id, Long depart_id, Long task_id) {
        MemberEntity isMember = userService.memberProcess(refresh, project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        TaskEntity isView = taskRepository.findById(task_id).orElse(null);
        log.info(isView.toString());

        return isView;
    }

    // 업무 수정.
    public TaskEntity updateProcess(String refresh, Long project_id, Long depart_id, Long task_id, TaskDTO taskDTO) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id) || !isMember.getRole().equals("MANAGER")) {
            System.out.println("=== exit ====");
            return null;
        }

        TaskEntity oldTask = taskRepository.findById(task_id).orElse(null);
        log.info(oldTask.toString());

        String newTitle = taskDTO.getTitle();
        String newDescription = taskDTO.getDescription();
        LocalDateTime newStart = taskDTO.getStart();
        LocalDateTime newEnd = taskDTO.getEnd();

        String newRelated = taskDTO.getRelated();
        String newDistribution = taskDTO.getDistribution();
        log.info(taskDTO.toString());

        // 업무 수정.
        TaskEntity newTask = new TaskEntity();
        newTask.setTitle(newTitle);
        newTask.setDescription(newDescription);
        newTask.setStart(newStart);
        newTask.setEnd(newEnd);
        newTask.setUpdate(LocalDateTime.now());

        newTask.setRelated(newRelated);
        log.info(newTask.toString());

        oldTask.patch(newTask);
        System.out.println("--- update task ----");
        System.out.println(oldTask.toString());
        System.out.println("--------------------");
        TaskEntity updated = taskRepository.save(oldTask);
        log.info(updated.toString());



        // 새로 배정.
        if (newDistribution != null) {
            // 배정했던 목록 삭제.
            distributionRepository.deleteAllByTask_Id(updated.getId());

            String[] distributionString = newDistribution.split(",");

            for (String member_id : distributionString) {
                MemberEntity distributionMember = memberRepository.findById(Long.parseLong(member_id)).orElse(null);
                log.info(distributionMember.toString());

                DistributionEntity isDistribution = new DistributionEntity();
                isDistribution.setMember(distributionMember);
                isDistribution.setTask(updated);
                DistributionEntity distributionSaved = distributionRepository.save(isDistribution);
                log.info(distributionSaved.toString());
            }
        }

        return updated;
    }

    // 업무 삭제.
    public TaskEntity deleteProcess(String refresh, Long project_id, Long depart_id, Long task_id) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id) || !isMember.getRole().equals("MANAGER")) {
            return null;
        }

        //삭제할 업무.
        TaskEntity deleteTask = taskRepository.findById(task_id).orElse(null);
        log.info(deleteTask.toString());

        // 업무 질문 삭제.
        questionRepository.deleteAllByTask_Id(deleteTask.getId());
        // 업무 배정 삭제.
        distributionRepository.deleteAllByTask_Id(deleteTask.getId());
        // 업무 삭제.
        taskRepository.deleteById(task_id);

        return deleteTask;
    }

    // 업무 제출.
    @Transactional
    public TaskEntity submitProcess(String refresh, Long project_id, Long depart_id, Long task_id, String approval) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        TaskEntity progressTask = taskRepository.findById(task_id).orElse(null);

        Boolean existMember = distributionRepository.existsByMember_IdAndTask_Id(isMember.getId(), progressTask.getId());

        if (!existMember) {
            return null;
        }



        taskRepository.updateSubmit(LocalDateTime.now(), approval, task_id);

        TaskEntity approved = taskRepository.findById(task_id).orElse(null);


        return approved;
    }

    // 업무 결재.
    public TaskEntity approveProcess(String refresh, Long project_id, Long depart_id, Long task_id, String approve_type) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id) || !isMember.getRole().equals("MANAGER")) {
            return null;
        }

        TaskEntity progressTask = taskRepository.findById(task_id).orElse(null);



        progressTask.setApprove(LocalDateTime.now());
        progressTask.setApproval(approve_type);


        System.out.println("===== task =====");
        System.out.println(progressTask.toString());
        TaskEntity approved = taskRepository.save(progressTask);
        log.info(approved.toString());

        return approved;
    }

    // 팀장의 각 팀원 업무 목록.
    public List<TaskEntity> managerListProcess(String refresh, Long project_id, Long depart_id, Long member_id) {
        // 1.배정 테이블에서 해당 멤버를 조회
        List<DistributionEntity> distributionEntityList = distributionRepository.findAllByMember_id(member_id);
        List<DistributionEntity> afterDistributionList = new ArrayList<>();
        // 2.각 배정 엔티티를 조회해서 project_id를 비교해서 해당 프로젝트인것만 넣기
        for(int i = 0; i < distributionEntityList.size(); i++){
            if(distributionEntityList.get(i).getTask().getDepart().getProject().getId() == project_id){
                afterDistributionList.add(distributionEntityList.get(i));
            }
        }
        // 3.배정엔티티를 돌면서 업무테이블에서 해당 프로젝트에서 멤버의 업무를 리스트에 추가
        List<TaskEntity> resultList = new ArrayList<>();
        for(int i = 0; i < afterDistributionList.size(); i++){
            TaskEntity entity = taskRepository.findById(afterDistributionList.get(i).getTask().getId()).orElse(null);
            if(entity != null){
                resultList.add(entity);
            }
        }
        // 4.모은 목록을 반환
        return resultList;
//        MemberEntity isMember = userService.memberProcess(refresh,project_id);
//
//        if (!isMember.getDepart().getId().equals(depart_id) || !isMember.getRole().equals("MANAGER")) {
//            return null;
//        }
//
//        MemberEntity isStaff = memberRepository.findById(member_id).orElse(null);
//        log.info(isStaff.toString());
//
//        // task_main의 member_id는 해당 업무를 생성한 유저의 id가 들어가기 때문에
//        // 아래 배열로 조회해서 보내면 member_id가 생성한 업무의 목록을 전달해주는 것
//        // 필요한 것 해당 member_id가 배정받은 업무 목록
//        List<TaskEntity> staffList = taskRepository.findAllByDepart_IdAndMember_Id(isStaff.getDepart().getId(), isStaff.getId());
//        log.info(staffList.toString());
//
//        return staffList;
    }

    // 각 팀원의 업무 목록.
    public List<TaskEntity> staffListProcess(String refresh, Long project_id, Long depart_id) {
        MemberEntity isMember = userService.memberProcess(refresh,project_id);

        if (!isMember.getDepart().getId().equals(depart_id)) {
            return null;
        }

        List<TaskEntity> staffList = taskRepository.findAllByDepartId(depart_id);
        log.info(staffList.toString());

        return staffList;
    }
}

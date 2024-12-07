package com.example.departorium.api;

import com.example.departorium.dto.TaskDTO;
import com.example.departorium.entity.TaskEntity;
import com.example.departorium.service.TaskService;
import com.example.departorium.service.UploadService;
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
public class TaskController {
    private final TaskService taskService;
    private final UploadService uploadService;

    public TaskController(TaskService taskService, UploadService uploadService) {
        this.taskService = taskService;
        this.uploadService = uploadService;
    }

    // 업무 생성.
    @PostMapping("/task/create/{project_id}/{depart_id}")
    public ResponseEntity<TaskEntity> create(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @RequestBody  TaskDTO taskDTO) {
        System.out.println("=== Task Create ===");
        System.out.println(taskDTO.toString());

        TaskEntity created = taskService.createProcess(refresh, project_id, depart_id, taskDTO);


        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 업무 접근.
    @GetMapping("/task/view/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<TaskEntity> view(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id) {
        TaskEntity viewed = taskService.viewProcess(refresh, project_id, depart_id, task_id);

        return (viewed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(viewed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 업무 수정.
    @PatchMapping("/task/update/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<TaskEntity> update(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id, @RequestBody TaskDTO taskDTO) {
        System.out.println("====== update =======");
        System.out.println(taskDTO.toString());
        TaskEntity updated = taskService.updateProcess(refresh, project_id, depart_id, task_id, taskDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 업무 삭제.
    @DeleteMapping("/task/delete/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<TaskEntity> delete(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id) {
        TaskEntity deleted = taskService.deleteProcess(refresh, project_id, depart_id, task_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 업무 제출.
    @PatchMapping("/task/submit/{project_id}/{depart_id}/{task_id}")
    public ResponseEntity<TaskEntity> submit(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id, @RequestParam String approval) {

        System.out.println("file : " + approval);
        TaskEntity submitted = taskService.submitProcess(refresh, project_id, depart_id, task_id, approval);

        return (submitted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(submitted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 업무 결재.
    @PatchMapping("/task/approval/{project_id}/{depart_id}/{task_id}/{approve_type}")
    public ResponseEntity<TaskEntity> approve(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long task_id, @PathVariable String approve_type) {
        TaskEntity approved = taskService.approveProcess(refresh, project_id, depart_id, task_id, approve_type);

        return (approved != null) ?
                ResponseEntity.status(HttpStatus.OK).body(approved) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 팀장의 각 팀원 업무 목록.
    @GetMapping("/task/list/manager/{project_id}/{depart_id}/{member_id}")
    public ResponseEntity<List<TaskEntity>> managerList(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @PathVariable Long member_id) {
        List<TaskEntity> listed = taskService.managerListProcess(refresh, project_id, depart_id, member_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 팀원 업무 목록.
    @GetMapping("/task/list/staff/{project_id}/{depart_id}")
    public ResponseEntity<List<TaskEntity>> staffList(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id) {
        List<TaskEntity> listed = taskService.staffListProcess(refresh, project_id, depart_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

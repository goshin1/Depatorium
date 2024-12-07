package com.example.departorium.api;


import com.example.departorium.dto.ProjectDTO;
import com.example.departorium.entity.ProjectEntity;
import com.example.departorium.service.ProjectService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // 프로젝트 생성.
    @PostMapping("/project/create")
    public ResponseEntity<ProjectEntity> create(@CookieValue("refresh") String refresh, @RequestBody ProjectDTO projectDTO) {
        ProjectEntity created = projectService.createProcess(refresh, projectDTO);

        return (created.getStatus().equals("ACTIVE")) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(created);
    }

    // 프로젝트 접근.
    @GetMapping("project/{project_id}")
    public ResponseEntity<ProjectEntity> view(@CookieValue("refresh") String refresh, @PathVariable Long project_id){
        ProjectEntity viewed = projectService.viewProcess(refresh, project_id);

        return (viewed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(viewed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 프로젝트 수정.
    @PatchMapping("/project/{project_id}/update")
    public ResponseEntity<ProjectEntity> update(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @RequestBody ProjectDTO projectDTO) {
        ProjectEntity updated = projectService.updateProcess(refresh, project_id, projectDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 프로젝트 삭제.
    @Transactional
    @DeleteMapping("/project/{project_id}/delete")
    public ResponseEntity<ProjectEntity> delete(@CookieValue("refresh") String refresh, @PathVariable Long project_id) {
        ProjectEntity deleted = projectService.deleteProcess(refresh, project_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 각 유저의 프로젝트 목록.
    @GetMapping("/user/project/list")
    public ResponseEntity<List<ProjectEntity>> userProjectList(@CookieValue("refresh") String refresh) {
        List<ProjectEntity> projectList = projectService.userProjectListProcess(refresh);

        return (projectList != null) ?
                ResponseEntity.status(HttpStatus.OK).body(projectList) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

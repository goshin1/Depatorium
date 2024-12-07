package com.example.departorium.api;


import com.example.departorium.dto.ScheduleDTO;
import com.example.departorium.entity.ScheduleEntity;
import com.example.departorium.service.ScheduleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // 메인 페이지의 개인 일정 생성.
    @PostMapping("/schedule/create")
    public ResponseEntity<ScheduleEntity> userScheduleCreate(@CookieValue(name = "refresh") String refresh, @RequestBody ScheduleDTO scheduleDTO) {
        System.out.println("---- dto ----");
        System.out.println(scheduleDTO.toString());
        ScheduleEntity created = scheduleService.createProcess(refresh, null, scheduleDTO);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 프로젝트 페이지의 프로젝트 일정 생성.
    @PostMapping("/schedule/create/{project_id}")
    public ResponseEntity<ScheduleEntity> projectScheduleCreate(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id, @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleEntity created = scheduleService.createProcess(refresh, project_id, scheduleDTO);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 일정 수정.
    @PatchMapping("/schedule/update/{schedule_id}")
    public ResponseEntity<ScheduleEntity> update(@CookieValue("refresh") String refresh, @PathVariable Long schedule_id, @RequestBody ScheduleDTO scheduleDTO) {
        ScheduleEntity updated = scheduleService.updateProcess(refresh, schedule_id, scheduleDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated):
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 일정 삭제.
    @DeleteMapping("/schedule/delete/{schedule_id}")
    public ResponseEntity<ScheduleEntity> delete(@CookieValue("refresh") String refresh, @PathVariable Long schedule_id) {
        ScheduleEntity deleted = scheduleService.deleteProcess(refresh, schedule_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 일정 접근.
    @GetMapping("/schedule/view/{schedule_id}")
    public ResponseEntity<ScheduleEntity> view(@CookieValue("refresh") String refresh, @PathVariable Long schedule_id) {
        ScheduleEntity viewed = scheduleService.viewProcess(refresh, schedule_id);

        return (viewed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(viewed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 메인 페이지의 개인 일정 목록.
    @GetMapping("/schedule/list/personal")
    public ResponseEntity<List<ScheduleEntity>> personalScheduleList(@CookieValue("refresh") String refresh) {
        System.out.println("=== refresh ===");
        System.out.println(refresh);
        List<ScheduleEntity> listed = scheduleService.personalListProcess(refresh);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 프로젝트 페이지의 프로젝트 일정 목록.
    @GetMapping("/schedule/list/project/{project_id}")
    public ResponseEntity<List<ScheduleEntity>> projectScheduleList(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id) {
        List<ScheduleEntity> listed = scheduleService.projectListProcess(refresh, project_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 프로젝트 페이지의 멤버 일정 목록.
    @GetMapping("/schedule/list/member/{project_id}")
    public ResponseEntity<List<ScheduleEntity>> memberScheduleList(@CookieValue(name = "refresh") String refresh, @PathVariable Long project_id) {
        List<ScheduleEntity> listed = scheduleService.memberScheduleListProcess(refresh, project_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

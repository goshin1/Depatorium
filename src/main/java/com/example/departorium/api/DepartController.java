package com.example.departorium.api;


import com.example.departorium.dto.DepartDTO;
import com.example.departorium.entity.DepartEntity;

import com.example.departorium.service.DepartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

@RestController
public class DepartController {


    private final DepartService departService;

    public DepartController(DepartService departService) {
        this.departService = departService;
    }

    // 직무회의실 생성.
    @PostMapping("/project/{project_id}/create")
    public ResponseEntity<DepartEntity> create(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @RequestBody DepartDTO departDTO) {
        DepartEntity created = departService.createProcess(refresh, project_id, departDTO);

        return (created != null) ?
                ResponseEntity.status(HttpStatus.OK).body(created) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 각 프로젝트의 직무회의실 목록.
    @GetMapping("/project/{project_id}/depart/list")
    public ResponseEntity<List<DepartEntity>> list(@PathVariable Long project_id){
        List<DepartEntity> listed = departService.listProcess(project_id);

        return (listed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(listed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 직무회의실 접근.
    @GetMapping("/project/{project_id}/{depart_id}")
    public ResponseEntity<DepartEntity> view(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id) {
        DepartEntity viewed = departService.viewProcess(refresh, project_id, depart_id);

        return (viewed != null) ?
                ResponseEntity.status(HttpStatus.OK).body(viewed) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 채팅 방 입장 전에 해당 유저가 해당 직무회의실에 배정 되었는지 확인
    @GetMapping("/project/approve/{project_id}/{depart_id}")
    public ResponseEntity<String> approve(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id){
        String approve = departService.approveDepart(refresh, project_id, depart_id);
        return ResponseEntity.status(HttpStatus.OK).body(approve);
    }

    // 직무회의실 수정.
    @PatchMapping("/project/{project_id}/{depart_id}/update")
    public ResponseEntity<DepartEntity> update(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id, @RequestBody DepartDTO departDTO) {
        DepartEntity updated = departService.updateProcess(refresh, project_id, depart_id, departDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 직무회의실 삭제.
    @Transactional
    @DeleteMapping("/project/{project_id}/{depart_id}/delete")
    public ResponseEntity<DepartEntity> delete(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @PathVariable Long depart_id) {
        DepartEntity deleted = departService.deleteProcess(refresh, project_id, depart_id);

        return (deleted != null) ?
                ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

//    @GetMapping("/num")
//    public String toToken(@RequestParam Long num){
//        return departService.toToken(num);
//    }



}

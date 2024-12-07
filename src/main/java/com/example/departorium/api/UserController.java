package com.example.departorium.api;


import com.example.departorium.dto.CheckDTO;
import com.example.departorium.dto.MemberDTO;
import com.example.departorium.dto.ProjectDTO;
import com.example.departorium.dto.UserDTO;
import com.example.departorium.entity.MemberEntity;
import com.example.departorium.entity.ProjectEntity;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/check")
    public ResponseEntity<String> checkUser(@CookieValue("refresh") String refresh, @RequestBody CheckDTO checkDTO){

        return userService.checkUser(refresh, checkDTO) != null ?
                ResponseEntity.status(HttpStatus.OK).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    // 로그인한 유저 정보.
    @GetMapping("/user/profile")
    public ResponseEntity<UserEntity> userProfile(@CookieValue("refresh") String refresh) {


        UserEntity profiled = userService.userProcess(refresh);

        return (profiled != null) ?
                ResponseEntity.status(HttpStatus.OK).body(profiled) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 로그인 한 유저의 프로필 수정.
    @PostMapping("/user/profile/update")
    public ResponseEntity<UserEntity> userProfileUpdate(@CookieValue("refresh") String refresh, @RequestBody UserDTO userDTO) {
        UserEntity updated = userService.updateProcess(refresh, userDTO);

        return (updated != null) ?
                ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }



    // 초대코드 입력.
    @PostMapping("/user/project/invitation")
    public ResponseEntity<MemberEntity> memberInvite(@CookieValue("refresh") String refresh, @RequestBody ProjectDTO projectDTO) {
        MemberEntity invited = userService.inviteProcess(refresh, projectDTO);

        return (invited != null) ?
                ResponseEntity.status(HttpStatus.OK).body(invited) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 각 프로젝트의 유저 목록
    @GetMapping("/project/{project_id}/user/list")
    public ResponseEntity<List<MemberEntity>> projectUserList(@PathVariable Long project_id) {
        List<MemberEntity> userList = userService.projectUserListProcess(project_id);

        return (userList != null) ?
                ResponseEntity.status(HttpStatus.OK).body(userList) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // 초대코드 입력한 유저 직무 배정.
    @PostMapping("/project/{project_id}/assign")
    public ResponseEntity<MemberEntity> memberAssign(@CookieValue("refresh") String refresh, @PathVariable Long project_id, @RequestBody MemberDTO memberDTO) {
        MemberEntity assigned = userService.assignProcess(refresh, project_id, memberDTO);

        return (assigned != null) ?
                ResponseEntity.status(HttpStatus.OK).body(assigned) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

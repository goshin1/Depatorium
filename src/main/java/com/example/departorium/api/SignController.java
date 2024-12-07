package com.example.departorium.api;


import com.example.departorium.dto.UserDTO;
import com.example.departorium.entity.TokenEntity;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.service.SignService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SignController {
    private final SignService signService;

    public SignController(SignService signService) {
        this.signService = signService;
    }

    @PostMapping("/join")
    public ResponseEntity<UserEntity> join(@RequestBody UserDTO userDTO) {
        UserEntity joined = signService.joinProcess(userDTO);

        return (joined != null) ?
                ResponseEntity.status(HttpStatus.OK).body(joined) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/join/google")
    public ResponseEntity<UserEntity> joinGoogle(@CookieValue("refresh") String refresh, @RequestBody UserDTO userDTO) {


        UserEntity joined = signService.joinGoogleProcess(refresh, userDTO);

        return (joined != null) ?
                ResponseEntity.status(HttpStatus.OK).body(joined) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/duplicate/{username}")
    public ResponseEntity<UserEntity> duplicate(@PathVariable String username) {
        System.out.println("username " + username);
        UserEntity duplicateCheck = signService.duplicateProcess(username);

        return (duplicateCheck == null) ?
                ResponseEntity.status(HttpStatus.OK).build() :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenEntity> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenEntity reissued = signService.reissueProcess(request,response);

        return (reissued != null) ?
                ResponseEntity.status(HttpStatus.OK).body(reissued) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}

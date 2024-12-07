package com.example.departorium.service;


import com.example.departorium.dto.UserDTO;
import com.example.departorium.entity.TokenEntity;
import com.example.departorium.entity.UserEntity;

import com.example.departorium.jwt.UtilityJWT;
import com.example.departorium.repository.TokenRepository;
import com.example.departorium.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SignService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UtilityJWT utilityJWT;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserService userService;

    public SignService(UserRepository userRepository, TokenRepository tokenRepository, UtilityJWT utilityJWT, BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.utilityJWT = utilityJWT;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userService = userService;
    }

    // 회원가입.
    public UserEntity joinProcess(UserDTO userDTO) {
        log.info(userDTO.toString());

        // 회원가입 정보.
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String nickname = userDTO.getNickname();
        String email = userDTO.getEmail();
        LocalDate birth = userDTO.getBirth();
        String phone = userDTO.getPhone();
        String group = userDTO.getGroup();
        Boolean existsUsername = userRepository.existsByUsername(username);

        // 이미 ID가 존재하면,
        if (existsUsername) {
            return null;
        }

        Boolean existsNickname = userRepository.existsByNickname(nickname);

        // 이미 닉네임이 존재하면,
        if (existsNickname) {
            return null;
        }

        // 회원가입 정보 저장.
        UserEntity isUser = new UserEntity();
        isUser.setUsername(username);
        isUser.setPassword(bCryptPasswordEncoder.encode(password));
        isUser.setNickname(nickname);
        isUser.setEmail(email);
        isUser.setBirth(birth);
        isUser.setPhone(phone);
        isUser.setGroup(group);
        isUser.setTheme("LIGHT");
        isUser.setUrl("C:\\Departorium\\Download");
        isUser.setRole("ROLE_USER");
        isUser.setStatus("ACTIVE");
        UserEntity saved = userRepository.save(isUser);
        log.info(saved.toString());

        return saved;
    }

    // username 중복 확인.
    public UserEntity duplicateProcess(String username) {
        UserEntity checkUser = userRepository.findByUsername(username).orElse(null);


        return checkUser;
    }

    // Refresh 토큰 생성.
    public TokenEntity reissueProcess(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return null;
        }

        try {
            utilityJWT.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return null;
        }

        String category = utilityJWT.getCategory(refresh);

        if (!category.equals("refresh")) {
            return null;
        }

        Boolean isExist = tokenRepository.existsByRefresh(refresh);
        if (!isExist) {
            return null;
        }

        String username = utilityJWT.getUsername(refresh);
        String role = utilityJWT.getRole(refresh);

        String newAccess = utilityJWT.createJwt("access", username, role, 600000L);
        String newRefresh = utilityJWT.createJwt("refresh", username, role, 86400000L);

        UserEntity isUser = userRepository.findByUsername(username).orElse(null);
        log.info(isUser.toString());

        tokenRepository.deleteByRefresh(refresh);
        TokenEntity isToken = addRefreshEntity(isUser, newRefresh, 86400000L);
        log.info(isToken.toString());

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return isToken;
    }

    // Refresh 토큰 저장.
    private TokenEntity addRefreshEntity(UserEntity isUser, String refresh, Long expiredMs) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setRefresh(refresh);
        tokenEntity.setExpiration(LocalDateTime.now());
        tokenEntity.setUser(isUser);

        return tokenRepository.save(tokenEntity);
    }

    // Cookie 생성.
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }

    public UserEntity joinGoogleProcess(String refresh, UserDTO userDTO) {
        UserEntity profiled = userService.userProcess(refresh);

        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String nickname = userDTO.getNickname();
        String email = userDTO.getEmail();
        LocalDate birth = userDTO.getBirth();
        String phone = userDTO.getPhone();
        String group = userDTO.getGroup();
        Boolean existsUsername = userRepository.existsByUsername(username);
        if (existsUsername) {
            return null;
        }

        UserEntity googleUser = new UserEntity();
        googleUser.setUsername(username);
        googleUser.setPassword(bCryptPasswordEncoder.encode(password));
        googleUser.setNickname(nickname);
        googleUser.setEmail(email);
        googleUser.setBirth(birth);
        googleUser.setPhone(phone);
        googleUser.setGroup(group);

        profiled.patch(googleUser);
        userRepository.save(profiled);
        return profiled;

    }
}

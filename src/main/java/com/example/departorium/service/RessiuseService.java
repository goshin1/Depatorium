package com.example.departorium.service;

import com.example.departorium.entity.TokenEntity;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.jwt.UtilityJWT;
import com.example.departorium.repository.TokenRepository;
import com.example.departorium.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class RessiuseService {

    private final UtilityJWT utilityJWT;
    private final TokenRepository tokenRepository;
    private final SignService signService;

    private final UserRepository userRepository;


    public RessiuseService(UtilityJWT utilityJWT, TokenRepository tokenRepository, SignService signService, UserRepository userRepository) {
        this.utilityJWT = utilityJWT;
        this.tokenRepository = tokenRepository;
        this.signService = signService;
        this.userRepository = userRepository;
    }


    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            utilityJWT.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        String category = utilityJWT.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = tokenRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = utilityJWT.getUsername(refresh);
        String role = utilityJWT.getRole(refresh);

        String newAccess = utilityJWT.createJwt("access", username, role, 600000L);
        String newRefresh = utilityJWT.createJwt("refresh", username, role, 86400000L);

        tokenRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 86400000L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        UserEntity user = userRepository.findByUsername(username).orElse(null);

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUser(user);
        tokenEntity.setRefresh(refresh);
        tokenEntity.setExpiration(LocalDateTime.now());

        tokenRepository.save(tokenEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }
}

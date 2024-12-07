package com.example.departorium.Filter;


import com.example.departorium.dto.UserDTO;
import com.example.departorium.entity.TokenEntity;
import com.example.departorium.entity.UserEntity;

import com.example.departorium.jwt.UtilityJWT;

import com.example.departorium.repository.TokenRepository;
import com.example.departorium.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UtilityJWT utilityJWT;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;

    public LoginFilter(AuthenticationManager authenticationManager, UtilityJWT jwtUtil, UserRepository userRepository, TokenRepository tokenRepository) {

        this.authenticationManager = authenticationManager;
        this.utilityJWT = jwtUtil;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserDTO userDTO = new UserDTO();

//        try {
//            ObjectMapper objectMapper = new ObjectMapper();
//            ServletInputStream inputStream = request.getInputStream();
//            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
//            userDTO = objectMapper.readValue(messageBody, UserDTO.class);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

//        String username = userDTO.getUsername();
//        String password = userDTO.getPassword();

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = utilityJWT.createJwt("access", username, role, 600000L);
        String refresh = utilityJWT.createJwt("refresh", username, role, 86400000L);

        UserEntity isUser = userRepository.findByUsername(username).orElse(null);
        addRefreshEntity(isUser, refresh);

        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
    }

    private void addRefreshEntity(UserEntity isUser, String refresh) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setRefresh(refresh);
        tokenEntity.setExpiration(LocalDateTime.now());
        tokenEntity.setUser(isUser);

        tokenRepository.save(tokenEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
}

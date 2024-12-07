package com.example.departorium.oauth2;


import com.example.departorium.dto.CustomOAuth2UserDetails;
import com.example.departorium.entity.TokenEntity;
import com.example.departorium.entity.UserEntity;
import com.example.departorium.jwt.UtilityJWT;
import com.example.departorium.repository.TokenRepository;
import com.example.departorium.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessOAuth2 extends SimpleUrlAuthenticationSuccessHandler {
    private final UtilityJWT utilityJWT;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public CustomSuccessOAuth2(UtilityJWT utilityJWT, UserRepository userRepository, TokenRepository tokenRepository) {
        this.utilityJWT = utilityJWT;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2UserDetails customOAuth2UserDetails = (CustomOAuth2UserDetails) authentication.getPrincipal();
        String username = customOAuth2UserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();
        String access = utilityJWT.createJwt("access", username, role, 600000L);
        String refresh = utilityJWT.createJwt("refresh", username, role, 86400000L);

        UserEntity isUser = userRepository.findByUsername(username).orElse(null);
        TokenEntity saveToken = addRefreshEntity(isUser, access);

        response.setHeader("access", access);
        response.addCookie(createCookie(saveToken.getRefresh()));

        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("/#/google");
    }

    private TokenEntity addRefreshEntity(UserEntity isUser, String refresh) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setRefresh(refresh);
        tokenEntity.setExpiration(LocalDateTime.now());
        tokenEntity.setUser(isUser);

        TokenEntity res = tokenRepository.save(tokenEntity);
        return res;
    }

    private Cookie createCookie(String value) {
        Cookie cookie = new Cookie("refresh", value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}

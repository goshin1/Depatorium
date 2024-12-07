package com.example.departorium.config;

import com.example.departorium.Filter.CustomLogoutFilter;
import com.example.departorium.Filter.LoginFilter;

import com.example.departorium.jwt.FilterJWT;
import com.example.departorium.jwt.UtilityJWT;

import com.example.departorium.oauth2.CustomSuccessOAuth2;
import com.example.departorium.repository.TokenRepository;
import com.example.departorium.repository.UserRepository;
import com.example.departorium.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final UtilityJWT utilityJWT;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessOAuth2 customSuccessOAuth2;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, UtilityJWT utilityJWT, CustomOAuth2UserService customOAuth2UserService, CustomSuccessOAuth2 customSuccessOAuth2, UserRepository userRepository, TokenRepository tokenRepository) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.utilityJWT = utilityJWT;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessOAuth2 = customSuccessOAuth2;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
                config.setAllowedMethods(Collections.singletonList("*"));
                config.setAllowCredentials(true);
                config.setAllowedHeaders(Collections.singletonList("*"));
                config.setMaxAge(3600L);
                config.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                config.setExposedHeaders(Collections.singletonList("access"));

                return config;
            }
        }));

        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests((request) -> request
                .requestMatchers("/", "/join", "/login", "/oauth2", "/reissue", "/#/").permitAll()

                .anyRequest().permitAll());

        http.oauth2Login((oauth2) -> oauth2.userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig.userService(customOAuth2UserService)).successHandler(customSuccessOAuth2));
//  .requestMatchers("/project/**", "/comment/**", "/user/**", "/question", "/schedule/**", "/task/**", "/upload").authenticated()
        http.addFilterBefore(new FilterJWT(utilityJWT), LoginFilter.class);
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), utilityJWT, userRepository, tokenRepository), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(utilityJWT, tokenRepository), LogoutFilter.class);


        http.addFilterAfter(new FilterJWT(utilityJWT), OAuth2LoginAuthenticationFilter.class);

        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
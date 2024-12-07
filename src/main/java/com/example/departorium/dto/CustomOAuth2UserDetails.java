package com.example.departorium.dto;


import com.example.departorium.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2UserDetails implements OAuth2User {
    private final UserEntity userEntity;

    public CustomOAuth2UserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {
        return userEntity.getNickname();
    }

    public String getUsername() {

        return userEntity.getUsername();
    }
}

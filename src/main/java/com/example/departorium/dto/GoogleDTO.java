package com.example.departorium.dto;

import java.util.Map;

public record GoogleDTO(Map<String, Object> attribute) implements com.example.departorium.dto.OAuth2DTO {
    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }
}

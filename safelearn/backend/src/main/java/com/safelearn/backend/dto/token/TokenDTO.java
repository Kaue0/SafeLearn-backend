package com.safelearn.backend.dto.token;

public record TokenDTO(

        String token,

        String email,

        Long userId,

        String username
) {}

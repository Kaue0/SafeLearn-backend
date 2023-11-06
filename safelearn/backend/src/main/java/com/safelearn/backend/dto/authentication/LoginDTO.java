package com.safelearn.backend.dto.authentication;

import jakarta.validation.constraints.NotEmpty;

public record LoginDTO(

        @NotEmpty
        String email,


        @NotEmpty
        String password
) {}


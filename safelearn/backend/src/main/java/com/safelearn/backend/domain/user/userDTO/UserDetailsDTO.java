package com.safelearn.backend.domain.user.userDTO;

import com.safelearn.backend.domain.user.User;

public record UserDetailsDTO(
        Long id,

        String name,

        String email,

        String username,

        String phone,

        String photo_link,

        String description,

        String ano,

        String materia
) {
    public UserDetailsDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPhone(),
                user.getPhoto_link(),
                user.getDescription(),
                user.getAno(),
                user.getMateria());
    }
}

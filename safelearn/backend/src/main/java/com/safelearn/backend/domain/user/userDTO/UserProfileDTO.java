package com.safelearn.backend.domain.user.userDTO;

import com.safelearn.backend.domain.user.User;

public record UserProfileDTO(
        Long id,

        String name,

        String username,

        String email,

        String phone,

        String photo_link,

        String description,

        String ano,

        String materia,

        int friendCount,

        boolean friends
) {
    public UserProfileDTO(User user, int friendCount, boolean friends) {
        this(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getPhoto_link(),
                user.getDescription(),
                user.getAno(),
                user.getMateria(),
                friendCount,
                friends);
    }
}

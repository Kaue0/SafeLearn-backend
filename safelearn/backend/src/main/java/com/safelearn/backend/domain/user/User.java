package com.safelearn.backend.domain.user;

import com.safelearn.backend.domain.user.userDTO.UpdateUserDTO;
import com.safelearn.backend.dto.authentication.RegisterDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "User")
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String phone;
    private String email;
    private String password;
    private String photo_link;
    private String description;
    private String ano;
    private String materia;
    private Boolean deleted = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
            name = "friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends;

    public User(RegisterDTO dto) {
        this.name = dto.name();
        this.username = dto.username();
        this.phone = dto.phone();
        this.email = dto.email();
        this.password = dto.password();
        this.photo_link = dto.photo_link();
        this.description = dto.description();
        this.ano = dto.ano();
        this.materia = dto.materia();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateUserData(UpdateUserDTO dto) {

        if (dto.name() != null) {
            this.name = dto.name();
        }

        if (dto.username() != null) {
            this.username = dto.username();
        }

        if (dto.email() != null) {
            this.email = dto.email();
        }

        if (dto.phone() != null) {
            this.phone = dto.phone();
        }

        if (dto.description() != null) {
            this.description = dto.description();
        }

        if (dto.photo_link() != null) {
            this.photo_link = dto.photo_link();
        }

        if (dto.ano() != null) {
            this.ano = dto.ano();
        }

        if (dto.materia() != null) {
            this.materia = dto.materia();
        }

        this.updatedAt = LocalDateTime.now();


    }
    public void newFriend(User friend) {
        this.friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
        friend.getFriends().remove(this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void delete() {
        this.deleted = true;
    }
}


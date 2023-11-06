package com.safelearn.backend.repository;

import com.safelearn.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByUsername(String username);

    User findUserByUsername(String username);

    UserDetails findByEmail(String email);

    User findById(String id);

    List<User> findByDeletedFalse();


    Boolean existsByUsername(String username);


    Boolean existsByEmail(String email);


    Boolean existsByPhone(String phone);

    @Query("SELECT friends FROM User u JOIN u.friends friends WHERE u.id = :id AND friends.deleted = false")
    List<User> findFriendsById(@Param("id") Long id);
}

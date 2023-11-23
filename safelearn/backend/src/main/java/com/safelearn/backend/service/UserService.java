package com.safelearn.backend.service;

import com.safelearn.backend.domain.user.User;
import com.safelearn.backend.domain.user.userDTO.UserDetailsDTO;
import com.safelearn.backend.domain.user.userDTO.UserProfileDTO;
import com.safelearn.backend.domain.user.userDTO.UpdateUserDTO;
import com.safelearn.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity detailUser(String username, Long loggedInUserId) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        if (user.getDeleted()) return ResponseEntity.status(HttpStatus.GONE).body("User got deleted");
        int friendCount = user.getFriends().size();

        boolean friends = false;
        if (loggedInUserId != null) {
            User loggedInUser = userRepository.findById(loggedInUserId).orElse(null);
            if (loggedInUser != null) {
                friends = loggedInUser.getFriends().contains(user);
            }
        }

        UserProfileDTO userProfile = new UserProfileDTO(user, friendCount, friends);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userProfile);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity detailUserById(String id, Long loggedInUserId) {
        User user = userRepository.findById(id);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        if (user.getDeleted()) return ResponseEntity.status(HttpStatus.GONE).body("User got deleted");
        int friendCount = user.getFriends().size();

        boolean friends = false;
        if (loggedInUserId != null) {
            User loggedInUser = userRepository.findById(loggedInUserId).orElse(null);
            if (loggedInUser != null) {
                friends = loggedInUser.getFriends().contains(user);
            }
        }

        UserProfileDTO userProfile = new UserProfileDTO(user, friendCount, friends);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userProfile);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<?> updateUser(UpdateUserDTO dto, Long id, Authentication authentication) {

        Optional<User> tempUser = userRepository.findById(id);
        if (tempUser.isEmpty()) {return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");}
        User user = tempUser.get();

        if (user.getDeleted()) {return ResponseEntity.status(HttpStatus.GONE).body("User got deleted.");}

        if (!((User) authentication.getPrincipal()).getId().equals(user.getId())) {return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized.");}

        if ((!Objects.equals(dto.username(), user.getUsername())) && userRepository.existsByUsername(dto.username())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        if ((!Objects.equals(dto.email(), user.getEmail())) && userRepository.existsByEmail(dto.email())) {
            System.out.println(dto.email());
            System.out.println(user.getEmail());
            return ResponseEntity.badRequest().body("Email already exists.");
        }
        if ((!Objects.equals(dto.phone(), user.getPhone())) && (dto.phone() != null) && userRepository.existsByPhone(dto.phone())) {
            return ResponseEntity.badRequest().body("Phone number already exists.");
        }

        if (dto.password() != null && !dto.password().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(dto.password());
            user.setPassword(encodedPassword);
        }

        user.updateUserData(dto);
        return ResponseEntity.ok().body(new UserDetailsDTO(user));
    }

    public ResponseEntity<?> deleteUser(Long id, Authentication authentication) {
        Optional<User> tmpUser = userRepository.findById(id);

        if (tmpUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = tmpUser.get();

        if (!((User) authentication.getPrincipal()).getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized");
        }
        user.delete();
        return ResponseEntity.ok().build();

    }

    public ResponseEntity updateInfo(Long id, Authentication authentication) {
        Optional<User> tempUser = userRepository.findById(id);
        User user = tempUser.get();
        if (!((User) authentication.getPrincipal()).getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User not authorized.");
        }

        return ResponseEntity.ok().body(new UpdateUserDTO(user));
    }


    public ResponseEntity<List<UserDetailsDTO>> getAllUsers() {

        List<User> users = userRepository.findByDeletedFalse();

        List<UserDetailsDTO> data = users.stream().map(UserDetailsDTO::new).toList();
        return ResponseEntity.ok().body(data);

    }

    public ResponseEntity<?> addFriend(Long userId, Long friendId) {
        Optional<User> tmpUser = userRepository.findById(userId);
        if (tmpUser.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        User user = tmpUser.get();

        Optional<User> optionalFriend = userRepository.findById(friendId);
        if (optionalFriend.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend not found.");
        User friend = optionalFriend.get();

        user.newFriend(friend);
        userRepository.save(user);

        return ResponseEntity.ok().body("Friend added successfully.");
    }

    public ResponseEntity<?> removeFriend(Long userId, Long friendId) {
        Optional<User> tmpUser = userRepository.findById(userId);
        if (tmpUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = tmpUser.get();

        Optional<User> optionalFriend = userRepository.findById(friendId);
        if (optionalFriend.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Friend not found.");
        }
        User friend = optionalFriend.get();

        user.removeFriend(friend);
        userRepository.save(user);

        return ResponseEntity.ok().body("Friend removed successfully.");
    }

    public ResponseEntity<?> listFriends(Long id) {
        List<User> friends = userRepository.findFriendsById(id);
        List<UserDetailsDTO> data = friends.stream().map(UserDetailsDTO::new).toList();
        return ResponseEntity.ok().body(data);
    }

}

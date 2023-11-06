package com.safelearn.backend.controller.auth;

import com.safelearn.backend.domain.user.User;
import com.safelearn.backend.domain.user.userDTO.UserDetailsDTO;
import com.safelearn.backend.dto.authentication.LoginDTO;
import com.safelearn.backend.dto.authentication.RegisterDTO;
import com.safelearn.backend.dto.token.TokenDTO;
import com.safelearn.backend.repository.UserRepository;
import com.safelearn.backend.service.TokenService;
import com.safelearn.backend.service.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity newLogin(@RequestBody @Valid LoginDTO data) {
        if (!userRepository.existsByEmail(data.email())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email does not exist");
        }
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var newAuthentication = authenticationManager.authenticate(authenticationToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) newAuthentication.getPrincipal();
            User authenticatingUser = (User) userRepository.findByUsername(userDetails.getUsername());

            if (authenticatingUser.getDeleted()) {
                return ResponseEntity.status(HttpStatus.GONE).body("User got deleted.");
            }

            if(passwordEncoder.matches(data.password(), authenticatingUser.getPassword())) {
                var newTokenJWT = tokenService.createToken(authenticatingUser);
                var responseTokenDTO = new TokenDTO(newTokenJWT, authenticatingUser.getEmail(), authenticatingUser.getId(), authenticatingUser.getUsername());
                return ResponseEntity.ok().body(responseTokenDTO);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email or password is invalid.");
            }
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Error in login.");
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity createUser(@Valid @RequestBody RegisterDTO data, UriComponentsBuilder UriBuilder) {

        // Verificação das credenciais
        if (userRepository.existsByEmail(data.email())) {
            return ResponseEntity.badRequest().body("Email already exists.");
        }
        if (userRepository.existsByUsername(data.username())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        if (data.phone() != null && data.phone() != "" && userRepository.existsByPhone(data.phone())) {
            return ResponseEntity.badRequest().body("Phone number already exists.");
        }
        if (data.ano() == null || data.materia() == null) {
            return ResponseEntity.badRequest().body("Data is missing. Please fill all the camps.");
        }

        var newUser = new User(data);

        // Codifique a senha
        newUser.setPassword(passwordEncoder.encode(data.password()));
        userRepository.save(newUser);
        var newUri = UriBuilder.path("/users/{id}").buildAndExpand(newUser.getId()).toUri();

        // Se tudo ocorrer bem, a resposta da api vai ser os dados do novo usuário no corpo da mensagem.
        return ResponseEntity.created(newUri).body(new UserDetailsDTO(newUser));

    }

}


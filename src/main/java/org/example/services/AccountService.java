package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.configuration.security.JwtService;
import org.example.dto.account.AuthResponseDto;
import org.example.dto.account.LoginDto;
import org.example.dto.account.RegistrationDto;
import org.example.entities.RoleEntity;
import org.example.entities.Roles;
import org.example.entities.UserEntity;
import org.example.entities.UserRoleEntity;
import org.example.repositories.RoleRepository;
import org.example.repositories.UserRepository;
import org.example.repositories.UserRoleRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDto register(RegistrationDto dto) {
        var user = UserEntity.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        userRepository.save(user);

        var role = roleRepository.findByName(Roles.User.toString());
        if (role == null) {
            role = RoleEntity.builder()
                    .name(Roles.User.toString())
                    .build();
            role = roleRepository.save(role);
        }

        var userRole = UserRoleEntity.builder()
                .role(role)
                .user(user)
                .build();
        userRoleRepository.save(userRole);

        var jwtToken = jwtService.generateAccessToken(user);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .message("User successfully registered")
                .build();
    }

    @Transactional
    public AuthResponseDto login(LoginDto dto) {
        var user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var isValid = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!isValid) {
            throw new UsernameNotFoundException("Invalid credentials");
        }
        var jwtToken = jwtService.generateAccessToken(user);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .message("User logged in successfully")
                .build();
    }

}

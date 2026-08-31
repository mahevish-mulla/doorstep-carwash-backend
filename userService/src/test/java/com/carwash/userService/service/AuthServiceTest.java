package com.carwash.userService.service;

import com.carwash.userService.dto.AuthResponse;
import com.carwash.userService.dto.LoginRequest;
import com.carwash.userService.dto.RegisterRequest;
import com.carwash.userService.entity.Role;
import com.carwash.userService.entity.User;
import com.carwash.userService.exception.EmailAlreadyExistsException;
import com.carwash.userService.exception.InvalidCredentialsException;
import com.carwash.userService.repository.UserRepository;
import com.carwash.userService.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("9876543210");

        savedUser = new User("Test User", "test@example.com", "encodedPass", "9876543210", Role.CUSTOMER);
        savedUser.setId(1L);
    }

    @Test
    void register_shouldSucceed_whenEmailNotTaken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(1L, "test@example.com", "CUSTOMER")).thenReturn("fake-jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertEquals("fake-jwt-token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldSucceed_whenCredentialsValid() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "encodedPass")).thenReturn(true);
        when(jwtUtil.generateToken(1L, "test@example.com", "CUSTOMER")).thenReturn("fake-jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertEquals("fake-jwt-token", response.getToken());
    }

    @Test
    void login_shouldThrow_whenPasswordWrong() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("wrongpass", "encodedPass")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
    }
}
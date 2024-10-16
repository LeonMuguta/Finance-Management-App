package com.finance.financial_management_app.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_UserAlreadyExists_ThrowsException() {
        // Arrange
        User existingUser = new User("John", "Doe", LocalDate.of(2000, 1, 1), Gender.MALE, "johndoe@gmail.com", "testpassword123");
        when(userRepository.findByEmail("johndoe@gmail.com")).thenReturn(Optional.of(existingUser));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(new User("John", "Doe", LocalDate.of(2000, 1, 1), Gender.MALE, "johndoe@gmail.com", "testpassword123"));
        });

        assertEquals("Email is already in use.", exception.getMessage());
    }

    @Test
    void registerUser_NewUser_SuccessfullyRegisters() {
        // Arrange
        User newUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "janedoe@gmail.com", "testpassword123");
        when(userRepository.findByEmail("janedoe@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser); // Match any User object

        // Act
        User result = userService.registerUser(newUser);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class)); // Verify save was called once with any User object


    }
    
}

package com.finance.financial_management_app.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    
    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    // TESTING THE POSTMAPPING METHOD SCENARIOS

    // Testing the PostMapping method in UserController.java to successfully register a user
    @Test
    void registerUser_ValidInput_ReturnsOK() throws Exception {
        // Arrange
        User newUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        when(userService.registerUser(any(User.class))).thenReturn(newUser);

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User Registered Successfully"));
        
    }

    // Testing the PostMapping method in UserController.java to try register a user and return a "Email is already in use exception"
    @Test
    void registerUser_UserAlreadyExists_ReturnsBadRequest() throws Exception {
        // Arrange
        User newUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Mock service behavior to throw exception for an existing user
        doThrow(new IllegalArgumentException("Email is already in use."))
                .when(userService).registerUser(any(User.class));

        // Act & Assert
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email is already in use."));
    }

    // TESTING THE PUTMAPPING METHOD SCENARIOS
    
    // Testing the PutMapping method in UserController.java to try and update a user's information and return "User Info Successfully Updated"
    @Test
    void updateUser_ValidInput_ReturnsOk() throws Exception {
        // Arrange
        User updatedUserDetails  = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(userService).updateUser(anyInt(), any(User.class));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDetails )))
                .andExpect(status().isOk())
                .andExpect(content().string("User Info Successfully Updated"));
    }

    // Testing the PutMapping method in UserController.java to try and update a user's birthday with an under 18 date and return "Date of birth cannot be updated after initial registration."
    @Test
    void updateUser_InvalidBirthDate_ReturnsBadRequest() throws Exception {
        // Arrange
        User updatedUserDetails  = new User("Jane", "Doe", LocalDate.of(2010, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Mock the service to do nothing (since the updateUser method is void)
        doThrow(new IllegalArgumentException("Date of birth cannot be updated after initial registration."))
                .when(userService).updateUser(anyInt(), any(User.class));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Date of birth cannot be updated after initial registration."));
    }

    // Testing the PutMapping method in UserController.java to try and update a user's email and return "Email cannot be updated after initial registration."
    @Test
    void updateUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        // Arrange
        User updatedUserDetails  = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Mock the service to do nothing (since the updateUser method is void)
        doThrow(new IllegalArgumentException("Email cannot be updated after initial registration."))
                .when(userService).updateUser(anyInt(), any(User.class));

        // Act & Assert
        mockMvc.perform(put("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email cannot be updated after initial registration."));
    }

    // TESTING THE DELETEMAPPING METHOD SCENARIOS

    // Testing the DeleteMapping method in UserController.java to try a delete a user's profile and return "User Successfully Deleted"
    @Test
    void deleteUser_ValidId_ReturnsOk() throws Exception {
        // Arrange
        User deleteUserDetails  = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");

        // Mock the repository to return the user when findById is called
        when(userRepository.findById(1)).thenReturn(Optional.of(deleteUserDetails));

        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(userRepository).delete(deleteUserDetails);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User Successfully Deleted"));
    }

    // Testing the DeleteMapping method in UserController.java to try a delete a user's profile and return "User Not Found"
    @Test
    void deleteUser_InvalidId_ReturnsBadRequest() throws Exception {
        // Arrange
        User deleteUserDetails  = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");

        // Mock the repository to return the user when findById is called
        when(userRepository.findById(15)).thenReturn(Optional.empty());

        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(userRepository).delete(deleteUserDetails);

        // Act & Assert
        mockMvc.perform(delete("/users/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User Not Found"));
    }

    // TESTING THE GETMAPPING METHOD SCENARIOS

    // Testing the GetMapping method in UserController.java to try return all users successfully
    @Test
    void getUsers_FindAll_ReturnsOk() throws Exception {
        // Mock the repository to return a list of all users on findAll is called
        when(userRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testing the GetMapping method in UserController.java to try return users by their ID successfully
    @Test
    void getUser_FindById_ReturnsOk() throws Exception {
        // Arrange: Create a mock user
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");

        // Mock the repository to return your mockUser when findById is called
        when(userRepository.findById(2)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(get("/users/{id}", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    // Testing the GetMapping method in UserController.java to return a UserNotFoundException as the specified Id doesn't exist
    @Test
    void getUser_FindById_ReturnsError() throws Exception {
        // Arrange: Mock the repository to return an empty Optional
        when(userRepository.findById(15)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/users/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }

    // Testing the GetMapping method in UserController.java to try return users by their ID successfully
    @Test
    void getUser_FindByEmail_ReturnsOk() throws Exception {
        // Arrange: Create a mock user
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");

        // Mock the repository to return your mockUser when findById is called
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(mockUser));

        // Act & Assert
        mockMvc.perform(get("/users/email/{email}", "jane@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.surname").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

}

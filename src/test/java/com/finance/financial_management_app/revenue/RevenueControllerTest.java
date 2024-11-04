package com.finance.financial_management_app.revenue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.Gender;
import com.finance.financial_management_app.user.UserRepository;
import com.finance.financial_management_app.user.UserService;

public class RevenueControllerTest {
    
    @InjectMocks
    private RevenueController revenueController;

    @Mock
    private RevenueService revenueService;

    @Mock
    private RevenueRepository revenueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(revenueController).build();
    }

    // TESTING THE POSTMAPPING METHOD SCENARIOS
    // Testing the PostMapping method in RevenueController.java to successfully create a user's revenue entry
    @Test
    void createRevenue_ValidInput_ReturnsOK() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(15000);
        Revenue newRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock revenue repository behavior
        when(revenueRepository.save(any(Revenue.class))).thenReturn(newRevenue); // Mock saving revenue
        
        // Create a new revenue entry
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2024-05-18");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/revenues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Revenue Transaction Successfully Created"));
    }

    // Testing the PostMapping method in RevenueController.java to return a bad request due to the date being in the future
    @Test
    void createRevenue_InvalidDate_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(15000);
        Revenue newRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock revenue repository behavior
        when(revenueRepository.save(any(Revenue.class))).thenReturn(newRevenue); // Mock saving revenue
        
        // Create a new revenue entry
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2025-05-18");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/revenues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The transaction date cannot be in the future"));
    }

    // Testing the PostMapping method in RevenueController.java to return a bad request due to the amount being a negative 
    @Test
    void createRevenue_InvalidAmount_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(-15000);
        Revenue newRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock revenue repository behavior
        when(revenueRepository.save(any(Revenue.class))).thenReturn(newRevenue); // Mock saving revenue
        
        // Create a new revenue entry
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2024-05-18");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/revenues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The amount cannot be negative."));   
    }

    // TESTING THE PUTTMAPPING METHOD SCENARIOS
    // Testing the PutMapping method in RevenueController.java to successfully update a revenue transaction
    @Test
    void updateRevenue_ValidInput_ReturnsOK() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(15000);
        Revenue existingRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock revenue repository behavior
        when(revenueRepository.findById(anyInt())).thenReturn(Optional.of(existingRevenue)); // Mock finding the existing revenue
        
        // Update an existing revenue entry
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2024-06-25");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/revenues/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Revenue Transaction Successfully Updated"));
    }

    // Testing the PutMapping method in RevenueController.java to return a BadRequest message due to the date being in the future 
    @Test
    void updateRevenue_InvalidDate_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(15000);
        // Revenue existingRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), mockUser);

        // Mock the repository behavior to find the user
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval

        // Use doThrow for void method
        doThrow(new IllegalArgumentException("The transaction date cannot be in the future."))
            .when(revenueService).updateRevenue(anyInt(), any());
        
        // Create the request payload with a future date
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2025-06-25");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/revenues/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The transaction date cannot be in the future."));
    }

    // Testing the PutMapping method in RevenueController.java to return a BadRequest message due to the amount being negative 
    @Test
    void updateRevenue_InvalidAmount_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(-15000);
        //Revenue existingRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), mockUser);

        // Mock the repository behavior to find the user and revenue
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval

        // Use doThrow for void method
        doThrow(new IllegalArgumentException("The amount cannot be negative."))
            .when(revenueService).updateRevenue(anyInt(), any());
        
        // Create the request payload with a future date
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("amount", amount);
        revenueData.put("category", "Dividends");
        revenueData.put("description", "Dividend payment from my investments");
        revenueData.put("date", "2024-06-25");
        revenueData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/revenues/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(revenueData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The amount cannot be negative."));
    }

    // TESTING THE DELETEMAPPING METHOD SCENARIOS
    // Testing the DeleteMapping method in RevenueController.java to return a successful message
    @Test
    void deleteRevenue_ValidId_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(-15000);
        Revenue existingRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock revenue repository behavior
        when(revenueRepository.findById(1)).thenReturn(Optional.of(existingRevenue));
        
        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(revenueRepository).delete(existingRevenue);
        
        // Act & Assert
        mockMvc.perform(delete("/revenues/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Revenue Transaction Successfully Deleted"));
    }

    // Testing the DeleteMapping method in RevenueController.java to return a "Revenue transaction does not exist" message
    @Test
    void deleteRevenue_InvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(-15000);
        Revenue existingRevenue = new Revenue(amount, "Dividends", "Dividend payment from my investments", LocalDate.of(2024, 5, 18), false, mockUser);

        // Mock revenue repository behavior
        when(revenueRepository.findById(1)).thenReturn(Optional.of(existingRevenue));
        
        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(revenueRepository).delete(existingRevenue);
        
        // Act & Assert
        mockMvc.perform(delete("/revenues/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Revenue transaction does not exist"));
    }

    // TESTING THE GETMAPPING METHOD SCENARIOS
    // Testing the GetMapping method in RevenueController.java to try return all revenue transactions successfully
    @Test
    void getRevenues_FindAll_ReturnsOk() throws Exception {
        // Mock the repository to return a list of all revenues when findAll is called
        when(revenueRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/revenues")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // Testing the GetMapping method in RevenueController.java to try return revenue transactions by UserId successfully
    @Test
    void getRevenues_FindByUserID_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123", false);
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        // Mock user repository behavior
        when(userService.findById(1)).thenReturn(mockUser); // Mock user retrieval

        // Act & Assert
        mockMvc.perform(get("/revenues/user/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

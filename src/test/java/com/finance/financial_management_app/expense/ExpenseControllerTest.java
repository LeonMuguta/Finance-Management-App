package com.finance.financial_management_app.expense;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.finance.financial_management_app.user.Gender;
import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;
import com.finance.financial_management_app.user.UserService;

public class ExpenseControllerTest {
    
    @InjectMocks
    private ExpenseController expenseController;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(expenseController).build();
    }

    // TESTING THE POSTMAPPING METHOD SCENARIOS
    // Testing the PostMapping method in ExpenseController.java to successfully create a user's expense entry
    @Test
    void createExpense_ValidInput_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1
        
        BigDecimal amount = new BigDecimal(7000);
        Expense newExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock expense repository behavior
        when(expenseRepository.save(any(Expense.class))).thenReturn(newExpense); // Mock saving expense
        
        // Create a new expense entry
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2024-05-18");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense Transaction Successfully Created"));
    }

    // Testing the PostMapping method in ExpenseController.java to return a bad request due to the date being in the future
    @Test
    void createExpense_InvalidDate_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(7000);
        Expense newExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval

        // Mock revenue repository behavior
        when(expenseRepository.save(any(Expense.class))).thenReturn(newExpense); // Mock saving revenue

        // Create a new expense entry
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2025-05-18");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The transaction date cannot be in the future"));
    }

    // Testing the PostMapping method in RevenueController.java to return a bad request due to the amount being a negative 
    @Test
    void createExpense_InvalidAmount_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(-7000);
        Expense newExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval

        // Mock revenue repository behavior
        when(expenseRepository.save(any(Expense.class))).thenReturn(newExpense); // Mock saving revenue

        // Create a new expense entry
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2024-05-18");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The amount cannot be negative."));
    }

    // TESTING THE PUTTMAPPING METHOD SCENARIOS
    // Testing the PutMapping method in ExpenseController.java to successfully update a expense transaction
    @Test
    void updateExpense_ValidInput_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(-7000);
        Expense existingExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock user repository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Mock revenue repository behavior
        when(expenseRepository.findById(anyInt())).thenReturn(Optional.of(existingExpense)); // Mock finding the existing revenue

        // Update an existing expense entry
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2024-06-25");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/expenses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense Transaction Successfully Updated"));
    }

    // Testing the PutMapping method in ExpenseController.java to return a BadRequest message due to the date being in the future 
    @Test
    void updateExpense_InvalidDate_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(7000);
        // Expense existingExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock the repository behavior to find the user and revenue
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Use doThrow for void method
        doThrow(new IllegalArgumentException("The transaction date cannot be in the future."))
            .when(expenseService).updateExpense(anyInt(), any());

        // Create the request payload with a future date
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2025-06-25");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/expenses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The transaction date cannot be in the future."));
    }

    // Testing the PutMapping method in ExpenseController.java to return a BadRequest message due to the amount being negative 
    @Test
    void updateExpense_InvalidAmount_ReturnsBadRequest() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(-7000);
        // Expense existingExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);

        // Mock the repository behavior to find the user and revenue
        when(userRepository.findById(1)).thenReturn(Optional.of(mockUser)); // Mock user retrieval
        
        // Use doThrow for void method
        doThrow(new IllegalArgumentException("The amount cannot be negative."))
            .when(expenseService).updateExpense(anyInt(), any());

        // Create the request payload with a future date
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put("amount", amount);
        expenseData.put("category", "Rent");
        expenseData.put("description", "Rent payment for my apartment");
        expenseData.put("date", "2024-06-25");
        expenseData.put("user", Map.of("id", mockUser.getId())); // Ensure this structure matches your controller's expectations
        
        // Act & Assert
        mockMvc.perform(put("/expenses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expenseData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The amount cannot be negative."));
    }

    // TESTING THE DELETEMAPPING METHOD 
    // Testing the DeleteMapping method in ExpenseController.java to return a successful message
    @Test
    void deleteExpense_ValidId_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(7000);
        Expense existingExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);
        
        // Mock expense repository behavior
        when(expenseRepository.findById(1)).thenReturn(Optional.of(existingExpense));

        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(expenseRepository).delete(existingExpense);

        // Act & Assert
        mockMvc.perform(delete("/expenses/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Expense Transaction Successfully Deleted"));
    }

    // Testing the DeleteMapping method in ExpenseController.java to return a "Expense transaction does not exist" message
    @Test
    void deleteExpense_InvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        BigDecimal amount = new BigDecimal(7000);
        Expense existingExpense = new Expense(amount, "Rent", "Rent payment for my apartment", LocalDate.of(2024, 5, 18), mockUser);
        
        // Mock expense repository behavior
        when(expenseRepository.findById(1)).thenReturn(Optional.of(existingExpense));

        // Mock the service to do nothing (since the updateUser method is void)
        doNothing().when(expenseRepository).delete(existingExpense);

        // Act & Assert
        mockMvc.perform(delete("/expenses/{id}", 15)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Expense transaction does not exist"));
    }

    // TESTING THE GETMAPPING METHOD SCENARIOS
    // Testing the GetMapping method in ExpenseController.java to try return all expense transactions successfully
    @Test
    void getExpense_FindAll_ReturnsOk() throws Exception {
        // Mock the repository to return a list of all expenses when findAll is called
        when(expenseRepository.findAll()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/expenses")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    // Testing the GetMapping method in ExpenseController.java to try return expense transactions by UserId successfully
    @Test
    void getExpenses_FindByUserID_ReturnsOk() throws Exception {
        // Arrange
        User mockUser = new User("Jane", "Doe", LocalDate.of(2000, 1, 1), Gender.FEMALE, "jane@example.com", "testpassword123");
        
        // Manually set the ID for mockUser
        mockUser.setId(1);  // Set the user ID to 1

        // Mock user repository behavior
        when(userService.findById(1)).thenReturn(mockUser); // Mock user retrieval

        // Act & Assert
        mockMvc.perform(get("/expenses/user/{userId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

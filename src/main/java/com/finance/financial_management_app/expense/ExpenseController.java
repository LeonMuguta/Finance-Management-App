package com.finance.financial_management_app.expense;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;
import com.finance.financial_management_app.user.UserService;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public ExpenseController(ExpenseService expenseService, ExpenseRepository expenseRepository, UserService userService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.expenseRepository = expenseRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Get all expense transactions
    @GetMapping("")
    public ResponseEntity<List<Expense>> findAll() {
        List<Expense> expenses = expenseRepository.findAll(); // Fetch all expense transactions from the repository
        return ResponseEntity.ok(expenses); // Return 200 OK with the list of transactions
    }

    // Get expense transactions by User Id
    @GetMapping("/user/{userId}")
    public List<Expense> getExpensesByUser(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        return expenseRepository.findByUser(user);
    }
    
    // Creating new expense transaction
    @PostMapping("")
    public ResponseEntity<String> addExpense(@RequestBody Map<String, Object> expenseData) {
        try {
            // Log the received expense data
            System.out.println("Received revenue data: " + expenseData);

            // Extract user ID from the request data
            @SuppressWarnings("unchecked")
            Integer userId = (Integer) ((Map<String, Object>) expenseData.get("user")).get("id");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Manually map the fields to the Expense object
            Expense expense = new Expense();

            // Parse and validate the amount to make sure it's not negative
            BigDecimal amount = new BigDecimal(expenseData.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                // return ResponseEntity.badRequest().body("The amount cannot be negative.");
                throw new IllegalArgumentException("The amount cannot be negative.");
            }

            // Validation - Parse the date and check if it's in the future
            LocalDate transactionDate = LocalDate.parse((String) expenseData.get("date"));
            if (transactionDate.isAfter(LocalDate.now())) {
                // return ResponseEntity.badRequest().body("The transaction date cannot be in the future");
                throw new IllegalArgumentException("The transaction date cannot be in the future");
            }

            // Set the values of the expense fields
            expense.setAmount(amount);
            expense.setCategory((String) expenseData.get("category"));
            expense.setDescription((String) expenseData.get("description"));
            expense.setDate(transactionDate);
            expense.setUser(user);
            
            expenseRepository.save(expense);
            return ResponseEntity.ok("Expense Transaction Successfully Created");
        } catch (IllegalArgumentException e) {
            // Log any exceptions for debugging
            System.err.println("Error creating expense: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update expense transaction details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateExpense(@PathVariable Integer id, @RequestBody Map<String, Object> expenseDetails) {
        try {
            expenseService.updateExpense(id, expenseDetails);
            return ResponseEntity.ok("Expense Transaction Successfully Updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete expense transactions by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        // Check if the expense transaction exists
        Optional<Expense> expense = expenseRepository.findById(id);

        if (expense.isPresent()) {
            // Delete the expense transaction
            expenseRepository.delete(expense.get());

            // Return success response
            return ResponseEntity.ok("Expense Transaction Successfully Deleted");
        } else {
            // Return 404 Not Found if the expense transaction does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Expense transaction does not exist");
        }
    }
}

package com.finance.financial_management_app.revenue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.finance.financial_management_app.budget.BudgetService;
import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;
import com.finance.financial_management_app.user.UserService;

@RestController
@RequestMapping("/revenues")
public class RevenueController {
    private final RevenueService revenueService;
    private final RevenueRepository revenueRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BudgetService budgetService;

    public RevenueController(RevenueService revenueService, RevenueRepository revenueRepository, UserService userService, UserRepository userRepository, BudgetService budgetService) {
        this.revenueService = revenueService;
        this.revenueRepository = revenueRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
    }

    // Get all revenue transactions
    @GetMapping("")
    public ResponseEntity<List<Revenue>> findAll() {
        List<Revenue> revenues = revenueRepository.findAll(); // Fetch all revenue transactions from the repository
        return ResponseEntity.ok(revenues);  // Return 200 OK with the list of transactions
    }

    // Get revenue transactions by User Id
    @GetMapping("/user/{userId}")
    public List<Revenue> getRevenuesByUser(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        return revenueRepository.findByUser(user);
    }

    // Creating new revenue transaction
    @PostMapping("")
    public ResponseEntity<String> addRevenue(@RequestBody Map<String, Object> revenueData) {
        try {
            // Log the received revenue data
            System.out.println("Received revenue data: " + revenueData);

            // Extract user ID from the request data
            @SuppressWarnings("unchecked")
            Integer userId = (Integer) ((Map<String, Object>) revenueData.get("user")).get("id");
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Manually map the fields to the Revenue object
            Revenue revenue = new Revenue();

            // Parse and validate the amount to make sure it's not negative
            BigDecimal amount = new BigDecimal(revenueData.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                // return ResponseEntity.badRequest().body("The amount cannot be negative.");
                throw new IllegalArgumentException("The amount cannot be negative.");
            }

            // Validation - Parse the date and check if it's in the future
            LocalDate transactionDate = LocalDate.parse((String) revenueData.get("date"));
            if (transactionDate.isAfter(LocalDate.now())) {
                // return ResponseEntity.badRequest().body("The transaction date cannot be in the future");
                throw new IllegalArgumentException("The transaction date cannot be in the future");
            }

            // Set the values of the revenue fields
            revenue.setAmount(amount);
            revenue.setCategory((String) revenueData.get("category"));
            revenue.setDescription((String) revenueData.get("description"));
            revenue.setDate(transactionDate);
            revenue.setIsRecurring((boolean) revenueData.get("isRecurring"));
            revenue.setUser(user);

            revenueRepository.save(revenue);
            budgetService.checkAndSendExpenseWarning(user);

            return ResponseEntity.ok("Revenue Transaction Successfully Created");
        } catch (IllegalArgumentException e) {
            // Log any exceptions for debugging
            System.err.println("Error creating revenue: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update revenue transaction details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRevenue(@PathVariable Integer id, @RequestBody Map<String, Object> revenueDetails) {
        try {
            revenueService.updateRevenue(id, revenueDetails);
            return ResponseEntity.ok("Revenue Transaction Successfully Updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Delete revenue transactions by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        // Check if the revenue transaction exists
        Optional<Revenue> revenue = revenueRepository.findById(id);

        if (revenue.isPresent()) {
            Revenue existingRevenue = revenue.get();

            // Delete the revenue transaction
            revenueRepository.delete(existingRevenue);

            budgetService.checkAndSendExpenseWarning(existingRevenue.getUser());
            
            // Return success response
            return ResponseEntity.ok("Revenue Transaction Successfully Deleted");
        } else {
            // Return 404 Not Found if the revenue transaction does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Revenue transaction does not exist");
        }
    }
}

package com.finance.financial_management_app.budget;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;

import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;
import com.finance.financial_management_app.user.UserService;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    private final BudgetRepository budgetRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public BudgetController(BudgetRepository budgetRepository, UserService userService, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    // Get all budget goals
    @GetMapping("")
    public ResponseEntity<List<Budget>> findAll() {
        List<Budget> budgets = budgetRepository.findAll();
        return ResponseEntity.ok(budgets);
    }

    // Get budget goals by User Id
    @GetMapping("/user/{userId}")
    public List<Budget> getBudgetGoalsByUser(@PathVariable Integer userId) {
        User user = userService.findById(userId);
        return budgetRepository.findByUser(user);
    }

    // Creating a new budget goal
    @PostMapping("")
    public ResponseEntity<String> addBudgetGoal(@RequestBody Map<String, Object> budgetData) {
        try {
            // Extract user ID from the request data
            @SuppressWarnings("unchecked")
            Integer userId = (Integer) ((Map<String, Object>) budgetData.get("user")).get("id");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Budget budget = new Budget();

            String monthString = (String) budgetData.get("month");
            if (monthString == null || monthString.isEmpty()) {
                throw new IllegalArgumentException("Month cannot be empty");
            }
            Month month;
            try {
                month = Month.valueOf(monthString.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid month value: " + monthString);
            }
            
            Integer year = (Integer) budgetData.get("year");
            if (year == null || year < 2020 || year > 2030) {
                throw new IllegalArgumentException("Year must be a valid four-digit integer between 2020 and 2030");
            }

            BigDecimal minRevenue = new BigDecimal(budgetData.get("minRevenue").toString());
            if (minRevenue.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Minimum revenue goal cannot be negative");
            }

            BigDecimal maxExpense = new BigDecimal(budgetData.get("maxExpense").toString());
            if (maxExpense.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Maximum expense goal cannot be negative");
            }

            BigDecimal netBalanceGoal = new BigDecimal(budgetData.get("netBalanceGoal").toString());
            if (netBalanceGoal.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Maximum expense goal cannot be negative");
            }

            budget.setMonth(month);
            budget.setYear(year);
            budget.setMinRevenue(minRevenue);
            budget.setMaxExpense(maxExpense);
            budget.setNetBalanceGoal(netBalanceGoal);
            budget.setUser(user);

            budgetRepository.save(budget);
            return ResponseEntity.ok("Budget Goal Successfully Created");
        } catch (IllegalArgumentException e) {
            // Log any exceptions for debugging
            System.err.println("Error creating budget goal: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update budget goal details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBudget(@PathVariable Integer id, @RequestBody Map<String, Object> budgetDetails) {
        try {
            // Check if the budget goal exists in the database
            Optional<Budget> existingBudget = budgetRepository.findById(id);

            if (existingBudget.isPresent()) {
                Budget budget = new Budget();

                // Extract user ID from the request data
                @SuppressWarnings("unchecked")
                Integer userId = (Integer) ((Map<String, Object>) budgetDetails.get("user")).get("id");

                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

                String monthString = (String) budgetDetails.get("month");
                if (monthString == null || monthString.isEmpty()) {
                    throw new IllegalArgumentException("Month cannot be empty");
                }
                Month month;
                try {
                    month = Month.valueOf(monthString.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body("Invalid month value: " + monthString);
                }
                
                Integer year = (Integer) budgetDetails.get("year");
                if (year == null || year < 2020 || year > 2030) {
                    throw new IllegalArgumentException("Year must be a valid four-digit integer between 2020 and 2030");
                }
    
                BigDecimal minRevenue = new BigDecimal(budgetDetails.get("minRevenue").toString());
                if (minRevenue.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Minimum revenue goal cannot be negative");
                }
    
                BigDecimal maxExpense = new BigDecimal(budgetDetails.get("maxExpense").toString());
                if (maxExpense.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Maximum expense goal cannot be negative");
                }
    
                BigDecimal netBalanceGoal = new BigDecimal(budgetDetails.get("netBalanceGoal").toString());
                if (netBalanceGoal.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Maximum expense goal cannot be negative");
                }

                budget.setMonth(month);
                budget.setYear(year);
                budget.setMinRevenue(minRevenue);
                budget.setMaxExpense(maxExpense);
                budget.setNetBalanceGoal(netBalanceGoal);
                budget.setUser(user);

                budgetRepository.save(budget);
                return ResponseEntity.ok("Budget Goal Successfully Updated");
            } else {
                // Return 404 Not Found if the revenue transaction does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget goal does not exist");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Delete budget goal by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        // Check if the budget goal exists
        Optional<Budget> budget = budgetRepository.findById(id);

        if (budget.isPresent()) {
            // Delete the budget goal
            budgetRepository.delete(budget.get());

            // Return success response
            return ResponseEntity.ok("Budget goal has been successfully deleted");
        } else {
            // Return 404 not found if the budget goal does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Budget goal does not exist");
        }
    }
    
    
}

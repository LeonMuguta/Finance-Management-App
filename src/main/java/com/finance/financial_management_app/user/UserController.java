package com.finance.financial_management_app.user;

import org.springframework.web.bind.annotation.*;

import com.finance.financial_management_app.expense.Expense;
import com.finance.financial_management_app.expense.ExpenseRepository;
import com.finance.financial_management_app.revenue.Revenue;
import com.finance.financial_management_app.revenue.RevenueRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;
    private final RevenueRepository revenueRepository;
    private final ExpenseRepository expenseRepository;

    public UserController(UserService userService, UserRepository userRepository, RevenueRepository revenueRepository, ExpenseRepository expenseRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.revenueRepository = revenueRepository;
        this.expenseRepository = expenseRepository;
    }

    // Get all users
    @GetMapping("")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userRepository.findAll(); // Fetch all users from the repository
        return ResponseEntity.ok(users);  // Return 200 OK with the list of users
    }

    // Get user by ID
    @GetMapping("/{id}")
    User findById(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }

        return user.get();
    }

    // Get user by email
    @GetMapping("/email/{email}")
    public ResponseEntity<User> findByEmail(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);

        // Check if the user exists
        if (user.isEmpty()) {
            throw new UserNotFoundException(); // Throw the custom exception if not found
        }

        return ResponseEntity.ok(user.get()); // Return the user if found
    }
    
    // Create new users
    @PostMapping("")
    public ResponseEntity<String> registerUser(@Validated(User.Create.class) @RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User Registered Successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Changing a users password
    @PostMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @Validated(User.Update.class) @RequestBody Map<String, String> passwordData) {
        try {
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            userService.changePassword(id, currentPassword, newPassword);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Update user details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Integer id, @Validated(User.Update.class) @RequestBody User userDetails) {
        try {
            userService.updateUser(id, userDetails);
            return ResponseEntity.ok("User Info Successfully Updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id) {
        // Check if the user exists
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            User existingUser = user.get();

            // Delete the revenue transactions linked to that specific user
            List<Revenue> revenues = revenueRepository.findByUser(existingUser);
            for (Revenue revenue : revenues) {
                revenueRepository.delete(revenue);
            }

            // Delete the expense transactions linked to that specific user
            List<Expense> expenses = expenseRepository.findByUser(existingUser);
            for (Expense expense : expenses) {
                expenseRepository.delete(expense);
            }

            // Delete the user
            userRepository.delete(existingUser);
            
            // Return success response
            return ResponseEntity.ok("User Successfully Deleted");
        } else {
            // Return 404 Not Found if the user does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
    }
    
}

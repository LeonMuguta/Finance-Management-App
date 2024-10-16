package com.finance.financial_management_app.user;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    
    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
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
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            userService.registerUser(user);
            return ResponseEntity.ok("User Registered Successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update user details
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
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
            // Delete the user
            userRepository.delete(user.get());
            
            // Return success response
            return ResponseEntity.ok("User Successfully Deleted");
        } else {
            // Return 404 Not Found if the user does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
    }
    
}

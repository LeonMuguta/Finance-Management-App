package com.finance.financial_management_app.login;

import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserService;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> userOptional = userService.findUserByEmailAndAuthenticate(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Returning user's details along with the success message
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("id", user.getId());
            response.put("firstName", user.getFirstName());
            response.put("surname", user.getSurname());  // Optional, if needed
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);  // Send the user details back to the client
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
    

}

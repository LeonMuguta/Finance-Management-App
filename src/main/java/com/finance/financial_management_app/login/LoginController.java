package com.finance.financial_management_app.login;

import java.util.*;
import java.time.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.finance.financial_management_app.security.EmailService;
import com.finance.financial_management_app.security.VerificationCodeGenerator;
import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserService;
import com.finance.financial_management_app.verify.VerifyCode;
import com.finance.financial_management_app.verify.VerifyCodeRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class LoginController {
    
    private UserService userService;
    private VerifyCodeRepository verifyCodeRepository;
    private EmailService emailService;
    private AuthenticationManager authenticationManager;
    private HttpSession session;

    public LoginController(UserService userService, VerifyCodeRepository verifyCodeRepository, EmailService emailService, AuthenticationManager authenticationManager, HttpSession session) {
        this.userService = userService;
        this.verifyCodeRepository = verifyCodeRepository;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.session = session;
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Optional<User> userOptional = userService.findUserByEmailAndAuthenticate(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Authenticate the user and set SecurityContextHolder
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            session.setAttribute("authenticatedUser", email);
            System.out.println("Session ID ----> " + session.getId());

            // Send email if user has 2FA enabled
            if (user.getTwoFactorAuth()) {
                // Generate verification code and set expiration time
                String verificationCode = VerificationCodeGenerator.generateVerificationCode();
                VerifyCode codeEntity = new VerifyCode(
                    verificationCode,
                    LocalDateTime.now().plusMinutes(5), // Code is valid for 5 minutes
                    user
                );
                verifyCodeRepository.save(codeEntity);

                // Send the verification code to the user's email
                emailService.sendVerificationCode(email, verificationCode);
            }
            
            // Returning user's details along with the success message
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Verification code sent to your email. Enter the code to complete login.");
            response.put("id", user.getId());
            response.put("firstName", user.getFirstName());
            response.put("surname", user.getSurname());
            response.put("email", user.getEmail());
            response.put("twoFactorAuth", user.getTwoFactorAuth());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
    
    // Logout endpoint
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        
        return ResponseEntity.ok("User logged out successfully.");
    }
    

}

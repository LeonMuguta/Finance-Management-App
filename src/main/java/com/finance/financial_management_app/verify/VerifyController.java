package com.finance.financial_management_app.verify;

import java.time.*;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
public class VerifyController {

    private final VerifyCodeRepository verifyCodeRepository;

    public VerifyController(VerifyCodeRepository verifyCodeRepository) {
        this.verifyCodeRepository = verifyCodeRepository;
    }

    // Get all verifications
    @GetMapping("")
    public ResponseEntity<List<VerifyCode>> findAll() {
        List<VerifyCode> verifications = verifyCodeRepository.findAll();
        return ResponseEntity.ok(verifications);
    }
    
    // Post successful verifications
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest verifyCodeRequest) {
        Optional<VerifyCode> codeOptional = verifyCodeRepository.findByUserIdAndCode(
                verifyCodeRequest.getUserId(),
                verifyCodeRequest.getCode()
            );

        if (codeOptional.isPresent() && codeOptional.get().getExpirationTime().isAfter(LocalDateTime.now())) {
            // Code is valid and not expired
            return ResponseEntity.ok("Login Successful");
        } else {
            return ResponseEntity.status(402).body("Invalid or expired verification code");
        }
        
    }
    
}

package com.finance.financial_management_app.security;

import java.security.SecureRandom;

public class VerificationCodeGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final int CODE_LENGTH = 6;
    
    public static String generateVerificationCode() {
        // Generate a 6-digit code
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i=0; i<CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
    
}

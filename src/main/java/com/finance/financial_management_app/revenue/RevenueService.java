package com.finance.financial_management_app.revenue;

import java.util.Map;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;

@Service
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final UserRepository userRepository;

    public RevenueService(RevenueRepository revenueRepository, UserRepository userRepository) {
        this.revenueRepository = revenueRepository;
        this.userRepository = userRepository;
    }

    // Updating revenue transaction details
    public void updateRevenue(Integer id, Map<String, Object> revenueDetails) {
        // Check if the revenue transaction exists in the database
        Optional<Revenue> existingRevenue = revenueRepository.findById(id);

        if (existingRevenue.isPresent()) {
            Revenue revenue = existingRevenue.get();

            // Extract user ID from the request data
            @SuppressWarnings("unchecked")
            Integer userId = (Integer) ((Map<String, Object>) revenueDetails.get("user")).get("id");

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Parse and validate the amount
            BigDecimal amount = new BigDecimal(revenueDetails.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("The amount cannot be negative.");
            }

            // Parse and validate the date
            LocalDate transactionDate = LocalDate.parse((String) revenueDetails.get("date"));
            if (transactionDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("The transaction date cannot be in the future.");
            }
            
            // Update the revenue fields
            revenue.setAmount(amount);
            revenue.setCategory((String) revenueDetails.get("category"));
            revenue.setDescription((String) revenueDetails.get("description"));
            revenue.setDate(transactionDate);
            revenue.setUser(user);
            
            revenueRepository.save(revenue);
            
        } else {
            throw new IllegalArgumentException("Transaction not found");
        }
    }
}

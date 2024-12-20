package com.finance.financial_management_app.revenue;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.finance.financial_management_app.budget.BudgetService;
import com.finance.financial_management_app.security.EmailService;
import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;

@Service
public class RevenueService {
    private final RevenueRepository revenueRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BudgetService budgetService;

    public RevenueService(RevenueRepository revenueRepository, UserRepository userRepository, EmailService emailService, BudgetService budgetService) {
        this.revenueRepository = revenueRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.budgetService = budgetService;
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
            revenue.setIsRecurring((boolean) revenueDetails.get("isRecurring"));
            revenue.setUser(user);
            
            revenueRepository.save(revenue);
            budgetService.checkAndSendExpenseWarning(user);
            
        } else {
            throw new IllegalArgumentException("Transaction not found");
        }
    }

    // Run this cron every day at 8am
    @Scheduled(cron = "0 0 8 * * *")
    public void addRecuringRevenues() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int currentDayOfMonth = today.getDayOfMonth();

        // Find all recurring revenues
        List<Revenue> recurringRevenues = revenueRepository.findByIsRecurring(true);

        for (Revenue recurringRevenue : recurringRevenues) {
            LocalDate originalDate = recurringRevenue.getDate();

            // Check if the current day matches the recurring day
            if (originalDate.getDayOfMonth() == currentDayOfMonth) {
                // Set the date to the current month
                LocalDate newTransactionDate = LocalDate.of(currentYear, currentMonth, originalDate.getDayOfMonth());

                // Ensure the new transaction date is not in the future
                if (!newTransactionDate.isAfter(today)) {
                    // First check if the same recurring revenue already exists for the current month
                    boolean exists = revenueRepository.existsByUserAndDateAndAmountAndCategory(
                        recurringRevenue.getUser(), 
                        newTransactionDate, 
                        recurringRevenue.getAmount(), 
                        recurringRevenue.getCategory()
                    );

                    // Create a new Revenue entry with updated date for the new month
                    if (!exists) {
                        Revenue newRevenue = new Revenue();
                        newRevenue.setAmount(recurringRevenue.getAmount());
                        newRevenue.setCategory(recurringRevenue.getCategory());
                        newRevenue.setDescription(recurringRevenue.getDescription());
                        newRevenue.setDate(newTransactionDate);
                        newRevenue.setIsRecurring(true); 
                        newRevenue.setUser(recurringRevenue.getUser());

                        // Save the recurring revenue transaction
                        revenueRepository.save(newRevenue);
                        System.out.println("Recurring entry made");

                        // Send email to notify user of recurring entry made
                        String emailBody = "Dear " + recurringRevenue.getUser().getFirstName() + " " + recurringRevenue.getUser().getSurname() + ",<br><br>" + 
                                            "A new recurring revenue transaction of R" + recurringRevenue.getAmount() + " with the category '" + recurringRevenue.getCategory() + "' was created for " + newTransactionDate + ". <br><br>" + 
                                            "<strong>Kind Regards</strong><br><strong>PerFinancial</strong>";
                        emailService.sendEmail(recurringRevenue.getUser().getEmail(), "PerFinancial - New Recurring Revenue Transaction", emailBody);
                        System.out.println("Email sent to user");
                    } else {
                        System.out.println("Recurring entry already exists for this month.");
                    }
                }
            }
        }
    }
}

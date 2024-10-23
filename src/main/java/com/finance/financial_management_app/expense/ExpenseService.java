package com.finance.financial_management_app.expense;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.finance.financial_management_app.user.User;
import com.finance.financial_management_app.user.UserRepository;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // Updating expense transaction details
    public void updateExpense(Integer id, Map<String, Object> expenseDetails) {
        // Check if the expense transaction exists in the databse
        Optional<Expense> existingExpense = expenseRepository.findById(id);

        if (existingExpense.isPresent()) {
            Expense expense = existingExpense.get();

            // Extract user ID from the request data
            @SuppressWarnings("unchecked")
            Integer userId = (Integer) ((Map<String, Object>) expenseDetails.get("user")).get("id");

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Parse and validate the amount
            BigDecimal amount = new BigDecimal(expenseDetails.get("amount").toString());
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("The amount cannot be negative.");
            }

            // Parse and validate the date
            LocalDate transactionDate = LocalDate.parse((String) expenseDetails.get("date"));
            if (transactionDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("The transaction date cannot be in the future.");
            }
            
            // Update the expense fields
            expense.setAmount(amount);
            expense.setCategory((String) expenseDetails.get("category"));
            expense.setDescription((String) expenseDetails.get("description"));
            expense.setDate(transactionDate);
            expense.setIsRecurring((boolean) expenseDetails.get("isRecurring"));
            expense.setUser(user);

            expenseRepository.save(expense);

        } else {
            throw new IllegalArgumentException("Transaction not found");
        }
    }

    // Run this cron every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    public void addRecuringExpenses() {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();
        int currentDayOfMonth = today.getDayOfMonth();

        // Find all recurring expenses
        List<Expense> recurringExpenses = expenseRepository.findByIsRecurring(true);

        for (Expense recurringExpense : recurringExpenses) {
            LocalDate originalDate = recurringExpense.getDate();

            // Check if the current day matches the recurring day
            if (originalDate.getDayOfMonth() == currentDayOfMonth) {
                // Set the date to the current month
                LocalDate newTransactionDate = LocalDate.of(currentYear, currentMonth, originalDate.getDayOfMonth());

                // Ensure the new transaction date is not in the future
                if (!newTransactionDate.isAfter(today)) {
                    // First check if the same recurring expense already exists for the current month
                    boolean exists = expenseRepository.existsByUserAndDateAndAmountAndCategory(
                        recurringExpense.getUser(), 
                        newTransactionDate, 
                        recurringExpense.getAmount(), 
                        recurringExpense.getCategory()
                    );

                    // Create a new Expense entry with updated date for the new month
                    if (!exists) {
                        Expense newExpense = new Expense();
                        newExpense.setAmount(recurringExpense.getAmount());
                        newExpense.setCategory(recurringExpense.getCategory());
                        newExpense.setDescription(recurringExpense.getDescription());
                        newExpense.setDate(newTransactionDate);
                        newExpense.setIsRecurring(true);
                        newExpense.setUser(recurringExpense.getUser());

                        // Save the recurring expense transaction
                        expenseRepository.save(newExpense);
                        System.out.println("Recurring entry made");
                    } else {
                        System.out.println("Recurring entry already exists for this month");
                    }
                }
            }
        }
    }
}

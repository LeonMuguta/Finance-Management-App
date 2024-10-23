package com.finance.financial_management_app.expense;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.finance.financial_management_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUser(User user);

    // Find all recurring expenses
    List<Expense> findByIsRecurring(boolean isRecurring);

    // Check if the recurring expense transaction already exists
    boolean existsByUserAndDateAndAmountAndCategory(User user, LocalDate date, BigDecimal bigDecimal, String category);
}

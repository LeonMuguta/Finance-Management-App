package com.finance.financial_management_app.expense;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.finance.financial_management_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUser(User user);

    // Find all recurring expenses
    List<Expense> findByIsRecurring(boolean isRecurring);

    // Check if the recurring expense transaction already exists
    boolean existsByUserAndDateAndAmountAndCategory(User user, LocalDate date, BigDecimal bigDecimal, String category);

    List<Expense> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE MONTH(e.date) = :month AND YEAR(e.date) = :year AND e.user = :user")
    double sumByMonthAndYear(@Param("month") int month, @Param("year") int year, @Param("user") User user);
}

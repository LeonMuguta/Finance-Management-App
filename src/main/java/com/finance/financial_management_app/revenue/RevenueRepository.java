package com.finance.financial_management_app.revenue;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.finance.financial_management_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {
    List<Revenue> findByUser(User user);

    // Find all recurring revenues
    List<Revenue> findByIsRecurring(boolean isRecurring);

    // Check if the recurring revenue transaction already exists
    boolean existsByUserAndDateAndAmountAndCategory(User user, LocalDate date, BigDecimal bigDecimal, String category);

    List<Revenue> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(r.amount) FROM Revenue r WHERE MONTH(r.date) = :month AND YEAR(r.date) = :year AND r.user = :user")
    double sumByMonthAndYear(@Param("month") int month, @Param("year") int year, @Param("user") User user);
}

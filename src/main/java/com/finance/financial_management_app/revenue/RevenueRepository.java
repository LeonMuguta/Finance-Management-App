package com.finance.financial_management_app.revenue;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.finance.financial_management_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {
    List<Revenue> findByUser(User user);

    // Find all recurring revenues
    List<Revenue> findByIsRecurring(boolean isRecurring);

    // Check if the recurring revenue transaction already exists
    boolean existsByUserAndDateAndAmountAndCategory(User user, LocalDate date, BigDecimal bigDecimal, String category);
}

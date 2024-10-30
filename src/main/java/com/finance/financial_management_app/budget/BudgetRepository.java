package com.finance.financial_management_app.budget;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.finance.financial_management_app.user.User;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUser(User user);

    List<Budget> findByMonth(Month month);

    List<Budget> findByYear(Integer year);

    Optional<Budget> findByUserAndMonthAndYear(User user, Month month, Integer year);

    // Method to find budget goals by month and year
    @Query("SELECT b FROM Budget b WHERE b.month = :month AND b.year = :year")
    List<Budget> findByMonthAndYear(@Param("month") Month month, @Param("year") Integer year);
}

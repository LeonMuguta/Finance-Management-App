package com.finance.financial_management_app.budget;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finance.financial_management_app.user.User;

public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findByUser(User user);

    List<Budget> findByMonth(Month month);

    List<Budget> findByYear(Integer year);
}

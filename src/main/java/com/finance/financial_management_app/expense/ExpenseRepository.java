package com.finance.financial_management_app.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.finance.financial_management_app.user.User;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findByUser(User user);
}

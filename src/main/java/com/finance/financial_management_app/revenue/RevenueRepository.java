package com.finance.financial_management_app.revenue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.finance.financial_management_app.user.User;

public interface RevenueRepository extends JpaRepository<Revenue, Integer> {
    List<Revenue> findByUser(User user);
}

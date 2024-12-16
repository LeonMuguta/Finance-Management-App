package com.finance.financial_management_app.verify;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finance.financial_management_app.user.User;


public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Integer> {
    Optional<VerifyCode> findByUserIdAndCode(Integer user, String code);

    List<VerifyCode> findByUser(User user);
}

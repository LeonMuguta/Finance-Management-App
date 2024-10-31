package com.finance.financial_management_app.verify;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Integer> {
    Optional<VerifyCode> findByUserIdAndCode(Integer user, String code);
}

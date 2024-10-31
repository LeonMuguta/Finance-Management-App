package com.finance.financial_management_app.verify;

public class VerifyCodeRequest {
    private Integer userId;
    private String code;

    // Getters
    public Integer getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    // Setters
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

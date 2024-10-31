package com.finance.financial_management_app.verify;

import jakarta.persistence.*;
import java.time.*;

import com.fasterxml.jackson.annotation.*;
import com.finance.financial_management_app.user.User;

@Entity
@Table(name = "verification_code")
public class VerifyCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public VerifyCode() {}

    @JsonCreator
    public VerifyCode(
        @JsonProperty("code") String code,
        @JsonProperty("expirationTime") LocalDateTime expirationTime,
        @JsonProperty("user") User user
    ) {
        this.code = code;
        this.expirationTime = expirationTime;
        this.user = user;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Custom setter to set only user ID
    public void setUserById(Integer userId) {
        this.user = new User();
        this.user.setId(userId);
    }
}

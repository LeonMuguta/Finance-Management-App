package com.finance.financial_management_app.budget;

import java.math.BigDecimal;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.financial_management_app.user.User;

@Entity
@Table(name = "monthly_budget_goal")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Month cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "month", nullable = false)
    private Month month;

    @NotNull(message = "Year cannot be null")
    @Column(name = "year", nullable = false)
    private Integer year;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "min_revenue_goal")
    private BigDecimal minRevenue;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "max_expense_goal")
    private BigDecimal maxExpense;

    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "net_balance_goal")
    private BigDecimal netBalanceGoal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Budget() {}

    @JsonCreator
    public Budget(
        @JsonProperty("month") Month month,
        @JsonProperty("year") Integer year,
        @JsonProperty("minRevenue") BigDecimal minRevenue,
        @JsonProperty("maxExpense") BigDecimal maxExpense,
        @JsonProperty("netBalanceGoal") BigDecimal netBalanceGoal,
        @JsonProperty("user") User user
    ) {
        this.month = month;
        this.year = year;
        this.minRevenue = minRevenue;
        this.maxExpense = maxExpense;
        this.netBalanceGoal = netBalanceGoal;
        this.user = user;

        if (month == null) {
            throw new IllegalArgumentException("Month cannot be empty!");
        }

        if (year == null) {
            throw new IllegalArgumentException("Year cannot be empty");
        }
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public Month getMonth() {
        return month;
    }

    public Integer getYear() {
        return year;
    }

    public BigDecimal getMinRevenue() {
        return minRevenue;
    }

    public BigDecimal getMaxExpense() {
        return maxExpense;
    }

    public BigDecimal getNetBalanceGoal() {
        return netBalanceGoal;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setMinRevenue(BigDecimal minRevenue) {
        this.minRevenue = minRevenue;
    }

    public void setMaxExpense(BigDecimal maxExpense) {
        this.maxExpense = maxExpense;
    }

    public void setNetBalanceGoal(BigDecimal netBalanceGoal) {
        this.netBalanceGoal = netBalanceGoal;
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

package com.finance.financial_management_app.revenue;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.financial_management_app.user.User;

@Entity
@Table(name = "Revenue")
public class Revenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Transaction amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "category")
    private String category;

    @Column(name = "description")
    private String description;

    @NotNull(message = "Transaction date cannot be null")
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "is_recurring", nullable = false)
    private boolean isRecurring;

    // Many-to-One relationship with User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Revenue() {}

    @JsonCreator
    public Revenue(
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("category") String category,
        @JsonProperty("description") String description,
        @JsonProperty("date") LocalDate date,
        @JsonProperty("isRecurring") boolean isRecurring,
        @JsonProperty("user") User user
    ) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        this.isRecurring = isRecurring;
        this.user = user;

        if (amount == null) {
            throw new IllegalArgumentException("Transaction amount cannot be empty!");
        }
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Transaction date cannot be in the future!");
        }
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean getIsRecurring() {
        return isRecurring;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setIsRecurring(boolean isRecurring) {
        this.isRecurring = isRecurring;
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

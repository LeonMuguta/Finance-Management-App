package com.finance.financial_management_app.user;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "Users")
public class User {
    public interface Create {}  // Validation group for creation
    public interface Update {}  // Validation group for update

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "First name cannot be empty")
    @Column(name = "first_name", nullable=false)
    private String firstName;

    @NotBlank(message = "Surname cannot be empty")
    @Column(name = "surname", nullable=false)
    private String surname;

    @NotNull(message = "Date of birth cannot be null")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable=false)
    private Gender gender;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be empty")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be empty", groups = Create.class)
    @Column(nullable = false)
    private String password;

    @Column(name = "two_factor_auth", nullable = false)
    private boolean twoFactorAuth;

    public User() {}

    @JsonCreator
    public User(
        @JsonProperty("firstName") String firstName,
        @JsonProperty("surname") String surname,
        @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
        @JsonProperty("gender") Gender gender,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password,
        @JsonProperty("twoFactorAuth") boolean twoFactorAuth
    ) {
        this.firstName = firstName;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.twoFactorAuth = twoFactorAuth;

        // Only enforce password requirement if it's a new user creation (password is required)
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future!");
        }
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getTwoFactorAuth() {
        return twoFactorAuth;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTwoFactorAuth(boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

}

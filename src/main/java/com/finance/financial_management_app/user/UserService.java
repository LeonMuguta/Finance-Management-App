package com.finance.financial_management_app.user;

import java.time.*;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registering a new user
    public User registerUser(User user) {
        // Check if a user with the same email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        // Check if the email already exists on the database
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
     
        }

        // Calculate the user's age
        LocalDate today = LocalDate.now();
        LocalDate dob = user.getDateOfBirth();

        // Ensure the user is 18 years or older
        if (Period.between(dob, today).getYears() < 18) {
            throw new IllegalArgumentException("User must be at least 18 years old to register");
        }

        // proceed to register if the validation passes
        User newUser = new User(
            user.getFirstName(),
            user.getSurname(),
            user.getDateOfBirth(),
            user.getGender(),
            user.getEmail(),
            passwordEncoder.encode(user.getPassword())
        );
        return userRepository.save(newUser);
    }
    
    // Updating user details
    public void updateUser(Integer id, User userDetails) {
        // Check if the user exists in the database
        Optional<User> existingUser = userRepository.findById(id);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Prevent the update of the date of birth and email
            if (!user.getDateOfBirth().equals(userDetails.getDateOfBirth())) {
                throw new IllegalArgumentException("Date of birth cannot be updated after initial registration.");
            }
            if (!user.getEmail().equals(userDetails.getEmail())) {
                throw new IllegalArgumentException("Email cannot be updated after initial registration.");
            }

            // Update only if the field is present in the request
            if (userDetails.getFirstName() != null) {
                user.setFirstName(userDetails.getFirstName());
            }

            if (userDetails.getSurname() != null) {
                user.setSurname(userDetails.getSurname());
            }

            if (userDetails.getGender() != null) {
                user.setGender(userDetails.getGender());
            }

            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Password should be encoded
            }
            
            userRepository.save(user);
            
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    // Method to find a user by ID
    public User findById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    // Method to autenticate user for login
    public Optional<User> findUserByEmailAndAuthenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);  // Return the user if authentication succeeds
            }
        }

        return Optional.empty();  // Return empty if authentication fails
    }

    // Method for changing a user's password
    public void changePassword(Integer id, String currentPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findById(id);
    
        if (userOptional.isPresent()) {
            User user = userOptional.get();
    
            // Verify if the current password matches
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                throw new IllegalArgumentException("Current password is incorrect.");
            }
    
            // Update with the new password (encode it)
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

}

-- Users table
CREATE TABLE IF NOT EXISTS Users (
    id INT NOT NULL AUTO_INCREMENT,
    first_name varchar(250) NOT NULL,
    surname varchar(250) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender varchar(10) NOT NULL,
    email varchar(50) NOT NULL UNIQUE,
    password varchar(250) NOT NULL,
    two_factor_auth BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id)
);

-- Revenues table
CREATE TABLE IF NOT EXISTS Revenue (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount decimal(15, 2) NOT NULL,
    category varchar(255),
    description TEXT,
    date DATE NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Expenses table
CREATE TABLE IF NOT EXISTS Expense (
    id INT AUTO_INCREMENT PRIMARY KEY,
    amount decimal(15, 2) NOT NULL,
    category varchar(255),
    description TEXT,
    date DATE NOT NULL,
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Monthly budget goals table
CREATE TABLE IF NOT EXISTS monthly_budget_goal (
    id INT AUTO_INCREMENT PRIMARY KEY,
    month VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    min_revenue_goal DECIMAL(15, 2),
    max_expense_goal DECIMAL(15, 2),
    net_balance_goal DECIMAL(15, 2),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
);

-- Verification code table
CREATE TABLE IF NOT EXISTS verification_code (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(id)
)

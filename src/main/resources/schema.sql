-- Active: 1765398826988@@127.0.0.1@5432@jbank@public

-- Schema for JBank Database

-- Base clients table (common fields for all client types)
CREATE TABLE clients (
    customer_id SERIAL PRIMARY KEY,
    client_type VARCHAR(10) NOT NULL CHECK (client_type IN ('PERSONAL', 'BUSINESS')),
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- Personal client specific fields
CREATE TABLE personal_clients (
    customer_id INT PRIMARY KEY,
    tax_id VARCHAR(15) NOT NULL UNIQUE,
    credit_score INT NOT NULL CHECK (credit_score BETWEEN 300 AND 850),
    yearly_income DECIMAL(12, 2) NOT NULL CHECK (yearly_income >= 0),
    total_debt DECIMAL(12, 2) NOT NULL CHECK (total_debt >= 0),
    FOREIGN KEY (customer_id) REFERENCES clients(customer_id) ON DELETE CASCADE
);

-- Business client specific fields
CREATE TABLE business_clients (
    customer_id INT PRIMARY KEY,
    ein VARCHAR(15) NOT NULL UNIQUE,
    business_type VARCHAR(50) NOT NULL,
    contact_person_name VARCHAR(50) NOT NULL,
    contact_person_title VARCHAR(50) NOT NULL,
    total_asset_value DECIMAL(15, 2) NOT NULL CHECK (total_asset_value >= 0),
    annual_revenue DECIMAL(15, 2) NOT NULL CHECK (annual_revenue >= 0),
    annual_profit DECIMAL(15, 2) NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES clients(customer_id) ON DELETE CASCADE
);

-- Base accounts table (common fields for all account types)
CREATE TABLE accounts (
    account_id SERIAL PRIMARY KEY,
    account_type VARCHAR(20) NOT NULL CHECK (account_type IN ('CHECKING', 'SAVINGS', 'CREDIT_LINE')),
    account_name VARCHAR(30) NOT NULL,
    balance DECIMAL(12, 2) NOT NULL
);

-- Checking account specific fields
CREATE TABLE checking_accounts (
    account_id INT PRIMARY KEY,
    overdraft_fee DECIMAL(10, 2) NOT NULL CHECK (overdraft_fee >= 0),
    overdraft_limit DECIMAL(10, 2) NOT NULL CHECK (overdraft_limit >= 0),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Savings account specific fields
CREATE TABLE savings_accounts (
    account_id INT PRIMARY KEY,
    interest_rate DECIMAL(5, 4) NOT NULL CHECK (interest_rate >= 0),
    withdrawal_limit INT NOT NULL CHECK (withdrawal_limit >= 0),
    withdrawal_counter INT NOT NULL DEFAULT 0 CHECK (withdrawal_counter >= 0),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Credit line specific fields
CREATE TABLE credit_lines (
    account_id INT PRIMARY KEY,
    credit_limit DECIMAL(12, 2) NOT NULL CHECK (credit_limit >= 0),
    interest_rate DECIMAL(5, 4) NOT NULL CHECK (interest_rate >= 0),
    min_payment_percentage DECIMAL(5, 2) NOT NULL CHECK (min_payment_percentage BETWEEN 0 AND 100),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
);

-- Junction table for many-to-many relationship (could support joint accounts in future iterations)
-- Note: ON DELETE RESTRICT for accounts ensures accounts are not deleted via CASCADE
-- Accounts must be explicitly validated and deleted through the service layer
CREATE TABLE client_accounts (
    customer_id INT NOT NULL,
    account_id INT NOT NULL,
    ownership_type VARCHAR(20) NOT NULL DEFAULT 'PRIMARY' CHECK (ownership_type IN ('PRIMARY', 'JOINT')),
    PRIMARY KEY (customer_id, account_id),
    FOREIGN KEY (customer_id) REFERENCES clients(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE RESTRICT
);

-- Possible future indexes for better performance? (Not needed for this scale but good to know)
-- CREATE INDEX idx_clients_type ON clients(client_type);
-- CREATE INDEX idx_personal_tax_id ON personal_clients(tax_id);
-- CREATE INDEX idx_business_ein ON business_clients(ein);
-- CREATE INDEX idx_accounts_type ON accounts(account_type);
-- CREATE INDEX idx_client_accounts_customer ON client_accounts(customer_id);
-- CREATE INDEX idx_client_accounts_account ON client_accounts(account_id);
-- Active: 1765398826988@@127.0.0.1@5432@jbank@public
-- Drop schema for JBank
-- Run this to reset your local development database

-- Drop tables in reverse order (child tables first, then parent tables)
DROP TABLE IF EXISTS client_accounts CASCADE;
DROP TABLE IF EXISTS credit_lines CASCADE;
DROP TABLE IF EXISTS savings_accounts CASCADE;
DROP TABLE IF EXISTS checking_accounts CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS business_clients CASCADE;
DROP TABLE IF EXISTS personal_clients CASCADE;
DROP TABLE IF EXISTS clients CASCADE;

-- -- Drop indexes (if they weren't dropped with CASCADE)
-- DROP INDEX IF EXISTS idx_clients_type;
-- DROP INDEX IF EXISTS idx_personal_tax_id;
-- DROP INDEX IF EXISTS idx_business_ein;
-- DROP INDEX IF EXISTS idx_accounts_type;
-- DROP INDEX IF EXISTS idx_client_accounts_customer;
-- DROP INDEX IF EXISTS idx_client_accounts_account;

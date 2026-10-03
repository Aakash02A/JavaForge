CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

CREATE TABLE IF NOT EXISTS expenses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    expense_date DATE NOT NULL
);

-- Example CRUD queries used by ExpenseDAO:
-- INSERT INTO expenses (description, category, amount, expense_date)
-- VALUES ('Weekly groceries', 'Food', 54.25, '2026-10-01');
--
-- SELECT id, description, category, amount, expense_date
-- FROM expenses WHERE id = 1;
--
-- SELECT id, description, category, amount, expense_date
-- FROM expenses ORDER BY expense_date DESC, id DESC;
--
-- UPDATE expenses
-- SET description = 'Team lunch', category = 'Dining', amount = 38.75,
--     expense_date = '2026-10-02'
-- WHERE id = 1;
--
-- DELETE FROM expenses WHERE id = 1;

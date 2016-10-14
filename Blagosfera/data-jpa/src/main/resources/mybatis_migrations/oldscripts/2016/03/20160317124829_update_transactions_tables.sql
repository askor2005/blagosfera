-- // update transactions tables
-- Migration SQL that makes the change goes here.

DROP TABLE payment_transaction_verifiable_logs;

DROP SEQUENCE seq_payment_transaction_verifiable_logs;

DROP TABLE move_transaction_verifiable_logs;

DROP SEQUENCE seq_move_transaction_verifiable_logs;

ALTER TABLE transactions DROP COLUMN discriminator;

ALTER TABLE transactions ADD COLUMN state TEXT NOT NULL DEFAULT '';

UPDATE transactions
SET state = 'HOLD'
WHERE status = 0;

UPDATE transactions
SET state = 'POST'
WHERE status = 1;

UPDATE transactions
SET state = 'REJECT'
WHERE status = 2;

ALTER TABLE accounts ADD COLUMN total_balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

ALTER TABLE accounts ADD COLUMN hold_balance NUMERIC(19, 2) NOT NULL DEFAULT 0.00;

UPDATE accounts
SET total_balance = coalesce((SELECT sum(t.amount)
                              FROM transactions t
                              WHERE (t.account_id = accounts.id) AND ((t.status = 1) OR (t.status = 0 AND t.amount < 0))), 0);

UPDATE accounts
SET hold_balance = coalesce((SELECT sum(t.amount)
                             FROM transactions t
                             WHERE (t.account_id = accounts.id) AND (t.status = 0 AND t.amount < 0)), 0);

-- //@UNDO
-- SQL to undo the change goes here.



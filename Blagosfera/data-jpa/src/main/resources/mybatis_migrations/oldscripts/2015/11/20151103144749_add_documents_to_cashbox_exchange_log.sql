-- // add documents to cashbox_exchange_log
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_exchange_log ADD COLUMN shareholder_membership_fee_statement_document_id BIGINT DEFAULT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN shareholder_membership_fee_protocol_document_id BIGINT DEFAULT NULL;

-- //@UNDO
-- SQL to undo the change goes here.



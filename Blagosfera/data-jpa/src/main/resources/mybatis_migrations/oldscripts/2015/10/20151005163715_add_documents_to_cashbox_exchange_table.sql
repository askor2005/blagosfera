-- // add documents to cashbox exchange table
-- Migration SQL that makes the change goes here.

ALTER TABLE cashbox_exchange_log ADD COLUMN sharer_contribution_statement_document_id BIGINT NOT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN shop_contribution_statement_document_id BIGINT NOT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN community_contribution_protocol_document_id BIGINT NOT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN sharer_refund_statement_document_id BIGINT NOT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN shop_refund_statement_document_id BIGINT NOT NULL;
ALTER TABLE cashbox_exchange_log ADD COLUMN community_refund_protocol_document_id BIGINT NOT NULL;

-- //@UNDO
-- SQL to undo the change goes here.



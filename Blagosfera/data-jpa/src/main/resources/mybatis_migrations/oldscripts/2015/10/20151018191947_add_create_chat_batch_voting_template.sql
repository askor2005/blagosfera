-- // add_create_chat_batch_voting_template
-- Migration SQL that makes the change goes here.

ALTER TABLE batch_voting_templates ADD COLUMN is_need_create_chat boolean;

-- //@UNDO
-- SQL to undo the change goes here.



-- // create_chat_file_messages
-- Migration SQL that makes the change goes here.

ALTER TABLE chat_messages ADD COLUMN file_message_state integer;
ALTER TABLE chat_messages ADD COLUMN file_date_update_percent timestamp without time zone;
ALTER TABLE chat_messages ADD COLUMN file_loaded_percent integer;
ALTER TABLE chat_messages ADD COLUMN file_message boolean;
ALTER TABLE chat_messages ADD COLUMN file_size bigint;

-- //@UNDO
-- SQL to undo the change goes here.



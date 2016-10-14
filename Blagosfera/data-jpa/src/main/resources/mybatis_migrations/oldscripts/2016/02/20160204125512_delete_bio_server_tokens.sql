-- // delete bio server tokens
-- Migration SQL that makes the change goes here.

DROP TABLE bio_server_tokens;
DROP SEQUENCE seq_bio_server_tokens;

-- //@UNDO
-- SQL to undo the change goes here.



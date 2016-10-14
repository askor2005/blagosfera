-- // sms verification code
-- Migration SQL that makes the change goes here.

ALTER TABLE finger_tokens
  ADD COLUMN sms_code TEXT;

-- //@UNDO
-- SQL to undo the change goes here.



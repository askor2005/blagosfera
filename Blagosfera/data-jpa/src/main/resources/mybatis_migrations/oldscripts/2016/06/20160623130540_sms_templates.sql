-- // sms templates
-- Migration SQL that makes the change goes here.

INSERT INTO ramera_texts (id, code, description, text, is_html)
VALUES (nextval('seq_ramera_texts'), 'SMS_VERIFICATION_CODE', 'СМС-код подтверждения',
        'Код подтверждения операции: %code%', FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



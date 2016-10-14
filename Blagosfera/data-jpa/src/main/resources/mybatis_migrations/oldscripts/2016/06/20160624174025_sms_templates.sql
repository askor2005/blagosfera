-- // sms templates
-- Migration SQL that makes the change goes here.

UPDATE ramera_texts
SET text = 'Код подтверждения операции в Системе БЛАГОСФЕРА: %code%. Никому другому не сообщайте этот код!'
WHERE code = 'SMS_VERIFICATION_CODE';

-- //@UNDO
-- SQL to undo the change goes here.



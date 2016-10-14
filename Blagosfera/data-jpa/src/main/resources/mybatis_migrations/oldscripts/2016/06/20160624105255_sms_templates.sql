-- // sms templates
-- Migration SQL that makes the change goes here.

INSERT INTO ramera_texts (id, code, description, text, is_html)
VALUES (nextval('seq_ramera_texts'), 'SMS_PHONE_VERIFICATION_CODE', 'СМС-код подтверждения номера телефона',
        'Вы инициировали процедуру подтверждения Вашего телефона %number% в Системе БЛАГОСФЕРА. Никому другому не сообщайте этот код! Код подтверждения: %code%',
        FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



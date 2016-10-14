-- // sms templates
-- Migration SQL that makes the change goes here.

INSERT INTO ramera_texts (id, code, description, text, is_html)
VALUES (nextval('seq_ramera_texts'), 'SMS_CONTACT_REQUEST', 'Запрос на добавление в список контактов',
        'Пользователь %senderName% (ЛИК %senderIkp%) хочет добавить Вас в список контактов', FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



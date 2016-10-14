-- // sms templates
-- Migration SQL that makes the change goes here.

INSERT INTO ramera_texts (id, code, description, text, is_html)
VALUES (nextval('seq_ramera_texts'), 'SMS_TRANSACTION_SUCCESS_PAYMENT_SYSTEM', 'Успешное пополнение счета',
        'Счет пополнен на %amount%Ра', FALSE);

INSERT INTO ramera_texts (id, code, description, text, is_html)
VALUES (nextval('seq_ramera_texts'), 'SMS_TRANSACTION_SUCCESS_USER', 'Успешный перевод от другого пользователя',
        'На ваш %accountType% поступил перевод от %senderNameGenitive% (ЛИК %senderIkp%) на сумму %amount%Ра. Баланс счета: %balance%Ра. Комментарий: %transactionComment%', FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



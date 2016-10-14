-- // add system setting cashbox session duration
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings (id, key, val, description)
VALUES (nextval('seq_system_settings'), 'cashbox.operator.session.duration.hours', '8',
        'Максимальная продолжительность сессии оператора-кассира (в часах)');

-- //@UNDO
-- SQL to undo the change goes here.



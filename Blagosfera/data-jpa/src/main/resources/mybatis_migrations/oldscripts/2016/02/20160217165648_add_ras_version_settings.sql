-- // add ras version settings
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings (id, key, val, description)
VALUES (nextval('seq_system_settings'), 'ras.version.min', '1.0.6', 'Минимальная совместимая версия RAS');

INSERT INTO system_settings (id, key, val, description)
VALUES (nextval('seq_system_settings'), 'ras.version.current', '1.0.6', 'Текущая версия RAS');

-- //@UNDO
-- SQL to undo the change goes here.



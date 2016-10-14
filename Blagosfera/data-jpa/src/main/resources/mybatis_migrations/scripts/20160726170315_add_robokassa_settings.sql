-- // add robokassa settings
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings (id, key, val, description)
  SELECT
    nextval('system_settings_id'),
    'robokassa.login',
    'blagosfera',
    'логин магазина в Robokassa'
  WHERE NOT exists(SELECT id
                   FROM system_settings
                   WHERE key = 'robokassa.login');

INSERT INTO system_settings (id, key, val, description)
  SELECT
    nextval('system_settings_id'),
    'robokassa.pass1',
    'aZY056BrWJPxcVr5ug9P',
    'пароль 1'
  WHERE NOT exists(SELECT id
                   FROM system_settings
                   WHERE key = 'robokassa.pass1');

INSERT INTO system_settings (id, key, val, description)
  SELECT
    nextval('system_settings_id'),
    'robokassa.pass2',
    'o5U3x5RwLhJJ8fgpl1AO',
    'пароль 2'
  WHERE NOT exists(SELECT id
                   FROM system_settings
                   WHERE key = 'robokassa.pass2');

INSERT INTO system_settings (id, key, val, description)
  SELECT
    nextval('system_settings_id'),
    'robokassa.test',
    '1',
    'тестовый режим (1 - включен, 0 - выключен)'
  WHERE NOT exists(SELECT id
                   FROM system_settings
                   WHERE key = 'robokassa.test');

-- //@UNDO
-- SQL to undo the change goes here.



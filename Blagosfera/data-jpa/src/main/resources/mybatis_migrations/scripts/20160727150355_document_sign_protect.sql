-- // document sign protect
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings (id, key, val, description)
  SELECT
    nextval('system_settings_id'),
    'document.sign.protected',
    '1',
    'требовать подтверждение при подписании документа'
  WHERE NOT exists(SELECT id
                   FROM system_settings
                   WHERE key = 'document.sign.protected');

-- //@UNDO
-- SQL to undo the change goes here.



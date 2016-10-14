-- // add ras download url
-- Migration SQL that makes the change goes here.

INSERT INTO system_settings (id, key, val, description)
VALUES (nextval('seq_system_settings'), 'ras.download.url', 'http://ra-dom.ru/ras/setup-ras.msi', 'URL для скачивания RAS');

-- //@UNDO
-- SQL to undo the change goes here.



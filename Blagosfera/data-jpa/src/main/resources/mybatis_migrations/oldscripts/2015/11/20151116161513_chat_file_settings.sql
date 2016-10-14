-- // chat_file_settings
-- Migration SQL that makes the change goes here.

  -- Настройки форматов файлов, разрешённых для загрузки в чат
  insert into system_settings (id, key, val, description)
  select nextval('seq_system_settings'), 'chat.file.extensions', 'pdf,jpg,txt', 'Настройки форматов файлов, разрешённых для загрузки в чат. Значения пишутся через запятую без пробелов'
  where not exists(select id from system_settings where key = 'chat.file.extensions');


  -- Настройка максимального размера файла для загрузки в чат
  insert into system_settings (id, key, val, description)
  select nextval('seq_system_settings'), 'chat.file.max.size', '10000', 'Максимальный размер файла для загрузки в чат в байтах'
  where not exists(select id from system_settings where key = 'chat.file.max.size');

-- //@UNDO
-- SQL to undo the change goes here.



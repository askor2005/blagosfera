-- // help window settings
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'help.window.height', '50', 'Высота(в процентах от экрана пользователя) окна справки'
where not exists(select id from system_settings where key = 'help.window.height');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'help.window.width', '30', 'Ширина(в процентах от экрана пользователя) окна справки'
where not exists(select id from system_settings where key = 'help.window.width');

-- //@UNDO
-- SQL to undo the change goes here.



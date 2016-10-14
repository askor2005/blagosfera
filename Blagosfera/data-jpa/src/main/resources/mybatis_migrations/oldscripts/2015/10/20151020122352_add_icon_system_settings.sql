-- // add icon system settings
-- Migration SQL that makes the change goes here.

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.min.width', '10', 'Минимальная ширина загружаемой иконки'
where not exists(select id from system_settings where key = 'img.icon.min.width');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.min.height', '10', 'Минимальная высота загружаемой иконки'
where not exists(select id from system_settings where key = 'img.icon.min.height');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.max.width', '1000', 'Максимальная ширина загружаемой иконки'
where not exists(select id from system_settings where key = 'img.icon.max.width');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.max.height', '1000', 'Максимальная высота загружаемой иконки'
where not exists(select id from system_settings where key = 'img.icon.max.height');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.max.uploadsize', '2m', 'Максимально возможный размер иконки, которую можно загрузить на сервер (1 - 1 килобайт, 1m - 1 мегабайт, 1g - 1 гигабайт)'
where not exists(select id from system_settings where key = 'img.icon.max.uploadsize');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.types_allowed', 'bmp,jpeg,jpg,png', 'Разрешенные типы для загрузки иконки'
where not exists(select id from system_settings where key = 'img.icon.types_allowed');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.width', '50', 'Требуемая ширина иконки'
where not exists(select id from system_settings where key = 'img.icon.width');

insert into system_settings (id, key, val, description)
select nextval('seq_system_settings'), 'img.icon.height', '50', 'Требуемая высота иконки'
where not exists(select id from system_settings where key = 'img.icon.height');

-- //@UNDO
-- SQL to undo the change goes here.



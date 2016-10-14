-- // add_system_settings_community_filling
-- Migration SQL that makes the change goes here.

insert into system_settings(id, key, val, description)
select nextval('seq_system_settings'), 'community.filling-threshold', '70', 'Минимальный порог заполнения объединения для сертификации'
where not exists (select id from system_settings where key = 'community.filling-threshold');

-- //@UNDO
-- SQL to undo the change goes here.



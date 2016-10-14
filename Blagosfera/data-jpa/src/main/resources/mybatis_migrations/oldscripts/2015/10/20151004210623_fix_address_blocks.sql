-- // fix_address_blocks
-- Migration SQL that makes the change goes here.

-- удаляем не нужные поля
delete from field_values where field_id in (select id from fields where internal_name in ('PERSON_REGISTRATION_ADDRESS_FULL_INFO', 'PERSON_ACTUAL_ADDRESS_FULL_INFO'));
delete from fields where internal_name in ('PERSON_REGISTRATION_ADDRESS_FULL_INFO', 'PERSON_ACTUAL_ADDRESS_FULL_INFO');

-- переименовываем поля с адресом
update fields set name = 'Адрес регистрации' where internal_name = 'GEO_LOCATION';
update fields set name = 'Фактический адрес' where internal_name = 'F_GEO_LOCATION';


-- //@UNDO
-- SQL to undo the change goes here.



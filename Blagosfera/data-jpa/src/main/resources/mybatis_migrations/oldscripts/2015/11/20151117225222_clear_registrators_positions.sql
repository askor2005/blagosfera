-- // clear_registrators_positions
-- Migration SQL that makes the change goes here.

-- Очищаем координаты регистраторов, для того, чтобы установить через сервис обновления
delete from field_values where field_id in (select id from fields where internal_name = 'REGISTRATOR_OFFICE_GEO_POSITION')

-- //@UNDO
-- SQL to undo the change goes here.



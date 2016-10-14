-- // alter_additional_sharer_fields
-- Migration SQL that makes the change goes here.

-- Обнуление баллов у полей фактического адреса пользователя
update fields set points = 0 where fields_group_id = (select id from fields_groups where internal_name = 'PERSON_ACTUAL_ADDRESS');


-- //@UNDO
-- SQL to undo the change goes here.



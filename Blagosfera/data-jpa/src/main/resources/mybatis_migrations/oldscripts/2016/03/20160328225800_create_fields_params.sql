-- // create_fields_params
-- Migration SQL that makes the change goes here.

-- Поля с минимальной  и максимальной длиной поля
ALTER TABLE fields ADD COLUMN min_size integer;
ALTER TABLE fields ADD COLUMN max_size integer;

-- Значения этих полей для объединения
update fields set min_size = 3, max_size = 1000 where internal_name = 'COMMUNITY_NAME';
update fields set max_size = 255, required = true where internal_name = 'COMMUNITY_BRIEF_DESCRIPTION';

-- //@UNDO
-- SQL to undo the change goes here.



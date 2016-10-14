-- // change_fields_points
-- Migration SQL that makes the change goes here.

update fields set points = 0 where internal_name = 'SECONDNAME';

-- //@UNDO
-- SQL to undo the change goes here.



-- // change_passport_points
-- Migration SQL that makes the change goes here.
update fields set points = 0 where internal_name='PASSPORT_SERIAL';


-- //@UNDO
-- SQL to undo the change goes here.



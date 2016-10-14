-- // set_points_empty_to_registration_fields
-- Migration SQL that makes the change goes here.

update fields set points = 0 where internal_name in ('PASSPORT_DIVISION', 'PERSON_INN');

-- //@UNDO
-- SQL to undo the change goes here.



-- // required_middle_name
-- Migration SQL that makes the change goes here.

update fields set required = false where internal_name = 'SECONDNAME'

-- //@UNDO
-- SQL to undo the change goes here.



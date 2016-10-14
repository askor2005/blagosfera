-- // update_address_points
-- Migration SQL that makes the change goes here.

update fields set points = 0 where type = 5;
update fields set points = 0 where type = 6;

-- //@UNDO
-- SQL to undo the change goes here.



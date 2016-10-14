-- // fix_sharer_status_with_no_reg_requests
-- Migration SQL that makes the change goes here.
update sharers s set status = 3 where (status = 5) and (not exists (select * from registration_requests where object_type = 'SHARER' and object_id = s.id and status = 0));


-- //@UNDO
-- SQL to undo the change goes here.



-- // set_logout_date_where_not_exists
-- Migration SQL that makes the change goes here.
update sharers set logout_date = registered_at where logout_date is null;

-- //@UNDO
-- SQL to undo the change goes here.



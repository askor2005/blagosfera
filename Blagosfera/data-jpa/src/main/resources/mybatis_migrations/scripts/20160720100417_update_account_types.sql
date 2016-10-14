-- // update_account_types
-- Migration SQL that makes the change goes here.
update account_types set name = 'Личный счёт' where position = 1;


-- //@UNDO
-- SQL to undo the change goes here.



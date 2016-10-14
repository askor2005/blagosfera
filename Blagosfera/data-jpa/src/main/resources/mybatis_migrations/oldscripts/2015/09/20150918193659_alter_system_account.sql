-- // alter_system_account
-- Migration SQL that makes the change goes here.

update system_accounts set name = 'Система БЛАГОСФЕРА' where id = 1;

-- //@UNDO
-- SQL to undo the change goes here.



-- // hide administration menu in new client
-- Migration SQL that makes the change goes here.

UPDATE nav_menu_item
SET visible = FALSE
WHERE title = 'Администрирование';

-- //@UNDO
-- SQL to undo the change goes here.



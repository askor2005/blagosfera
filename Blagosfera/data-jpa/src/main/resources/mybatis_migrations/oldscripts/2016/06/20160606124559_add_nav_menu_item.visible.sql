-- // add nav_menu_item.visible
-- Migration SQL that makes the change goes here.

ALTER TABLE nav_menu_item
  ADD COLUMN visible BOOLEAN NOT NULL DEFAULT TRUE;

-- //@UNDO
-- SQL to undo the change goes here.



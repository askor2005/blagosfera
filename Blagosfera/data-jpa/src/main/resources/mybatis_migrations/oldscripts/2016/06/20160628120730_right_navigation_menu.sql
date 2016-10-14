-- // right navigation menu
-- Migration SQL that makes the change goes here.

ALTER TABLE nav_menu_item
  DROP COLUMN default_route;

ALTER TABLE nav_menu_item
  ADD COLUMN "type" CHARACTER VARYING(255) NOT NULL DEFAULT 'MAIN';

-- //@UNDO
-- SQL to undo the change goes here.



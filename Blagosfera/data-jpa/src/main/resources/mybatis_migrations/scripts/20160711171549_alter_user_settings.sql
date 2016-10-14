-- // alter_user_settings
-- Migration SQL that makes the change goes here.

ALTER TABLE sharer_settings
   ALTER COLUMN val TYPE character varying(1000000);

-- //@UNDO
-- SQL to undo the change goes here.



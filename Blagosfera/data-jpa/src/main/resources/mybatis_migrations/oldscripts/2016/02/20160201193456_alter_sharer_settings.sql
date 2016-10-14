-- // alter_sharer_settings
-- Migration SQL that makes the change goes here.

ALTER TABLE sharer_settings
   ALTER COLUMN val TYPE character varying(100000);

-- //@UNDO
-- SQL to undo the change goes here.



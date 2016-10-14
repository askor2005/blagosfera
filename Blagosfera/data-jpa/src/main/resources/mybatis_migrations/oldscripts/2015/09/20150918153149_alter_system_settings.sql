-- // alter_system_settings
-- Migration SQL that makes the change goes here.

ALTER TABLE system_settings
   ALTER COLUMN val TYPE character varying(10000);

ALTER TABLE system_settings
   ALTER COLUMN description TYPE character varying(10000);

-- //@UNDO
-- SQL to undo the change goes here.



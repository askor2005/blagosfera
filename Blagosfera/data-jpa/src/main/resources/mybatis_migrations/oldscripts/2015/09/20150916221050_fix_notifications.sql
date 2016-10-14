-- // fix_notifications
-- Migration SQL that makes the change goes here.

ALTER TABLE notifications
   ALTER COLUMN subject TYPE character varying(1000);


-- //@UNDO
-- SQL to undo the change goes here.



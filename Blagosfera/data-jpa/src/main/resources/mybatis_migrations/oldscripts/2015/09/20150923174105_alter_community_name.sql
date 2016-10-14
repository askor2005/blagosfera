-- // alter_community_name
-- Migration SQL that makes the change goes here.

ALTER TABLE communities
   ALTER COLUMN name TYPE character varying(1000);

-- //@UNDO
-- SQL to undo the change goes here.



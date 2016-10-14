-- // alter_flow_of_document
-- Migration SQL that makes the change goes here.

ALTER TABLE flowofdocument
   ALTER COLUMN name TYPE character varying(10000);

ALTER TABLE flowofdocument
   ALTER COLUMN short_name TYPE character varying(10000);


-- //@UNDO
-- SQL to undo the change goes here.



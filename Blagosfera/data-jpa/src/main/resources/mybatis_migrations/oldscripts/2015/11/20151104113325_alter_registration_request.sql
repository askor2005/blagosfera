-- // alter_registration_request
-- Migration SQL that makes the change goes here.

ALTER TABLE registration_requests RENAME user_id TO object_id;
ALTER TABLE registration_requests DROP CONSTRAINT fk_i4n2skrj5gaof4g51e7tdhgny;
ALTER TABLE registration_requests ADD COLUMN object_type character varying(50);
update registration_requests set object_type = 'SHARER';

-- //@UNDO
-- SQL to undo the change goes here.



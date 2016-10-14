-- // add ECO_ADVISOR_ADMIN role
-- Migration SQL that makes the change goes here.

INSERT INTO roles (id, name) VALUES (nextval('seq_roles'), 'ECO_ADVISOR_ADMIN');

-- //@UNDO
-- SQL to undo the change goes here.



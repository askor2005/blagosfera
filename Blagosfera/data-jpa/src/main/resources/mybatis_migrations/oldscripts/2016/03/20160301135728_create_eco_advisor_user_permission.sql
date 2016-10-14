-- // create eco_advisor_user permission
-- Migration SQL that makes the change goes here.

INSERT INTO community_permissions (id, name, position, title, description, security_role)
VALUES (nextval('seq_community_permissions'), 'ECO_ADVISOR_USER', 61, 'Доступ к ЭКС', 'Доступ к ЭКС', FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



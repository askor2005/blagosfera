-- // add section active sessions
-- Migration SQL that makes the change goes here.

INSERT INTO sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
VALUES
  (nextval('seq_sections'), '/admin/activeSessions', 'adminActiveSessions', 8, 'Активные сессии', 677, NULL, NULL, TRUE,
   NULL, NULL, NULL, NULL, 2);

-- //@UNDO
-- SQL to undo the change goes here.



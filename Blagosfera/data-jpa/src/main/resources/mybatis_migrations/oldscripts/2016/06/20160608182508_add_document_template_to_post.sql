-- // add_document_template_to_post
-- Migration SQL that makes the change goes here.

ALTER TABLE community_posts ADD COLUMN template_id bigint;

ALTER TABLE community_posts
 ADD CONSTRAINT community_posts_template_id_fk FOREIGN KEY (template_id)
 REFERENCES documents_templates (id) MATCH SIMPLE
 ON UPDATE NO ACTION ON DELETE NO ACTION;

-- //@UNDO
-- SQL to undo the change goes here.



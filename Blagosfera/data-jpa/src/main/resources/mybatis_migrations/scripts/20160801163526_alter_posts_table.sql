-- // alter_posts_table
-- Migration SQL that makes the change goes here.

CREATE TABLE public.community_post_document_template
(
  community_post_id bigint NOT NULL,
  document_template_id bigint NOT NULL,
  CONSTRAINT fk_post_document_template_document_template_id FOREIGN KEY (document_template_id)
      REFERENCES public.document_template_settings (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_post_document_template_community_post_id FOREIGN KEY (community_post_id)
      REFERENCES public.community_posts (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.community_post_document_template
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.



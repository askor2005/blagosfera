-- // batch_voting_template_alter
-- Migration SQL that makes the change goes here.

CREATE TABLE public.batch_voting_templates_to_batch_voting
(
  batch_voting_id bigint NOT NULL,
  batch_voting_template_id bigint NOT NULL,
  CONSTRAINT batch_voting_templates_to_batch_voting_pkey PRIMARY KEY (batch_voting_id, batch_voting_template_id),
  CONSTRAINT fk_batch_voting_template_id FOREIGN KEY (batch_voting_template_id)
      REFERENCES public.batch_voting_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.batch_voting_templates_to_batch_voting
  OWNER TO kabinet;


ALTER TABLE public.batch_voting_templates ADD COLUMN last_batch_voting_date timestamp without time zone;

-- //@UNDO
-- SQL to undo the change goes here.



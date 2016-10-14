-- // create_tables_for_template_settings
-- Migration SQL that makes the change goes here.

CREATE TABLE public.community_document_request
(
  id bigint NOT NULL,
  community_id bigint NOT NULL,
  user_id bigint NOT NULL,
  CONSTRAINT community_document_request_pkey PRIMARY KEY (id),
  CONSTRAINT fk_60xuux8sbq8yi5frg21rdm6hd FOREIGN KEY (user_id)
      REFERENCES public.sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_cul9y6dfs14ksyh6r5eciumcw FOREIGN KEY (community_id)
      REFERENCES public.communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.community_document_request
  OWNER TO kabinet;

CREATE SEQUENCE public.seq_community_document_request
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.seq_community_document_request
  OWNER TO kabinet;







CREATE TABLE public.community_document_request_document
(
  request_id bigint NOT NULL,
  document_id bigint NOT NULL,
  CONSTRAINT fk_1gxrw53pxh5rcswdjd6pg2beh FOREIGN KEY (request_id)
      REFERENCES public.community_document_request (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_86v78mxit0xgndchtl8v7ecl5 FOREIGN KEY (document_id)
      REFERENCES public.flowofdocument (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.community_document_request_document
  OWNER TO kabinet;





CREATE TABLE public.document_template_settings
(
  id bigint NOT NULL,
  template_id bigint NOT NULL,
  CONSTRAINT document_template_settings_pkey PRIMARY KEY (id),
  CONSTRAINT fk_gja3b368esrlbb3c4fm8w6d97 FOREIGN KEY (template_id)
      REFERENCES public.documents_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.document_template_settings
  OWNER TO kabinet;

CREATE SEQUENCE public.seq_document_template_settings
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.seq_document_template_settings
  OWNER TO kabinet;





CREATE TABLE public.document_template_participant_settings
(
  id bigint NOT NULL,
  type character varying(255) NOT NULL,
  source_id bigint,
  source_name character varying(1000),
  data_source_id bigint NOT NULL,
  setting_id bigint NOT NULL,
  CONSTRAINT document_template_participant_settings_pkey PRIMARY KEY (id),
  CONSTRAINT fk_6ea6nuui17a27wc9i8lssi8vw FOREIGN KEY (setting_id)
      REFERENCES public.document_template_settings (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s0w4orchi8x0qm0xnfie78cpv FOREIGN KEY (data_source_id)
      REFERENCES public.documents_types_participants (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE public.document_template_participant_settings
  OWNER TO kabinet;

CREATE SEQUENCE public.seq_document_template_participant_settings
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;
ALTER TABLE public.seq_document_template_participant_settings
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.



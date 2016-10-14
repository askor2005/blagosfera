-- // create_letter_of_authority
-- Migration SQL that makes the change goes here.

DO $$
    BEGIN
        BEGIN
	    ALTER TABLE flowofdocument ADD COLUMN active boolean;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column active already exists in flowofdocument.';
        END;
        BEGIN
	    ALTER TABLE flowofdocument ADD COLUMN expired_date timestamp without time zone;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column active expired_date exists in flowofdocument.';
        END;
    END;
$$;

CREATE TABLE IF NOT EXISTS letter_of_authority_role
(
  id bigint NOT NULL,
  document_script character varying(100000),
  key character varying(500),
  name character varying(500),
  scope_role_name character varying(500),
  scope_role_type character varying(500),
  scope_type character varying(100),
  list_editor_item_id bigint,
  CONSTRAINT letter_of_authority_role_pkey PRIMARY KEY (id),
  CONSTRAINT fk_ti3dnndrfyf86kl1ww674354r FOREIGN KEY (list_editor_item_id)
      REFERENCES list_editor_item (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_aa1k4jfcg5sw8eailmhfy2uav UNIQUE (key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE letter_of_authority_role
  OWNER TO kabinet;

CREATE TABLE IF NOT EXISTS letters_of_authorities
(
  id bigint NOT NULL,
  scope_type character varying(50),
  scope_id bigint,
  delegate_id bigint NOT NULL,
  document_id bigint NOT NULL,
  role_id bigint NOT NULL,
  owner_id bigint NOT NULL,
  CONSTRAINT letters_of_authorities_pkey PRIMARY KEY (id),
  CONSTRAINT fk_eevjdn8a7wxj2afnin7psplri FOREIGN KEY (owner_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_g8jxu31odylndd3tek5g3kgxo FOREIGN KEY (document_id)
      REFERENCES flowofdocument (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_mxocugtmvdqldf85f15qrb4i7 FOREIGN KEY (role_id)
      REFERENCES letter_of_authority_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ovkiod20uljcbc0yjlqea9jq FOREIGN KEY (delegate_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE letters_of_authorities
  OWNER TO kabinet;

DROP SEQUENCE IF EXISTS seq_letters_of_authorities;
CREATE SEQUENCE seq_letters_of_authorities START 100;

DROP SEQUENCE IF EXISTS seq_letter_of_authority_role;
CREATE SEQUENCE seq_letter_of_authority_role START 100;


DO $$
DECLARE
sectionId bigint;
BEGIN

  select id into sectionId from sections where name = 'radom';

  insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  select nextval('seq_sections'), null, 'letterOfAuthority', 5, 'Доверенности', sectionId, null, null, true, null, null, null, null, 0
  where not exists(select id from sections where name = 'letterOfAuthority');

  select id into sectionId from sections where name = 'letterOfAuthority';

  insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  select nextval('seq_sections'), '/letterofauthority/myLetterOfAuthority.html', 'myLetterOfAuthority', 0, 'Доверенности выданные мне', sectionId, null, null, true, null, null, null, null, 0
  where not exists(select id from sections where name = 'myLetterOfAuthority');

  insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  select nextval('seq_sections'), '/letterofauthority/ownerLetterOfAuthority.html', 'ownerLetterOfAuthority', 1, 'Доверенности выданные мной', sectionId, null, null, true, null, null, null, null, 0
  where not exists(select id from sections where name = 'ownerLetterOfAuthority');

  select id into sectionId from sections where name = 'adminSections';

  insert into sections (id, link, name, position, title, parent_id, hint, icon, published, page_id, help_link, application_id, image_url, type)
  select nextval('seq_sections'), '/admin/letterofauthority/adminPage.html', 'adminLetterOfAuthorityRoles', 7, 'Роли доверенностей', sectionId, null, null, true, null, null, null, null, 0
  where not exists(select id from sections where name = 'adminLetterOfAuthorityRoles');

END $$;

-- //@UNDO
-- SQL to undo the change goes here.



-- // create_batch_voting_template_tables
-- Migration SQL that makes the change goes here.

CREATE SEQUENCE seq_batch_voting_templates START 1;
CREATE TABLE batch_voting_templates
(
  id bigint NOT NULL,
  behavior character varying(255) NOT NULL,
  end_date timestamp without time zone NOT NULL,
  is_can_finish_before_end_date boolean NOT NULL,
  is_need_add_additional_votings boolean NOT NULL,
  mode integer NOT NULL,
  quorum bigint NOT NULL,
  secret_voting boolean NOT NULL,
  start_date timestamp without time zone NOT NULL,
  subject character varying(255) NOT NULL,
  voters_registration_end_date timestamp without time zone NOT NULL,
  voting_restart_count integer NOT NULL,
  community_id bigint,
  creator_id bigint,
  CONSTRAINT batch_voting_templates_pkey PRIMARY KEY (id),
  CONSTRAINT fk_5wm8k97wkhqhgvcv1f7i1a4ye FOREIGN KEY (creator_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_7h0rytn5uurrb77etfr0q3ljc FOREIGN KEY (community_id)
      REFERENCES communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE batch_voting_templates
  OWNER TO kabinet;

CREATE SEQUENCE seq_batch_voting_attribute_templates START 1;
CREATE TABLE batch_voting_attribute_templates
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  value character varying(255) NOT NULL,
  batch_voting_id bigint NOT NULL,
  CONSTRAINT batch_voting_attribute_templates_pkey PRIMARY KEY (id),
  CONSTRAINT fk_qxsdr4t2qla7tr2kqx9axwpnb FOREIGN KEY (batch_voting_id)
      REFERENCES batch_voting_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE batch_voting_attribute_templates
  OWNER TO kabinet;

CREATE SEQUENCE seq_voting_templates START 1;
CREATE TABLE voting_templates
(
  id bigint NOT NULL,
  description character varying(255) NOT NULL,
  index integer NOT NULL,
  is_fail_on_contra_result boolean NOT NULL,
  is_visible boolean NOT NULL,
  is_vote_cancellable boolean NOT NULL,
  is_vote_comments_allowed boolean NOT NULL,
  max_selection_count bigint NOT NULL,
  subject character varying(255) NOT NULL,
  voting_state integer NOT NULL,
  voting_type integer NOT NULL,
  batch_voting_id bigint,
  CONSTRAINT voting_templates_pkey PRIMARY KEY (id),
  CONSTRAINT fk_drxdn1y7vigea88qepkjk66kw FOREIGN KEY (batch_voting_id)
      REFERENCES batch_voting_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE voting_templates
  OWNER TO kabinet;

CREATE TABLE voters_allowed_templates
(
  batch_voting_id bigint NOT NULL,
  voter_id bigint
)
WITH (
  OIDS=FALSE
);
ALTER TABLE voters_allowed_templates
  OWNER TO kabinet;

CREATE SEQUENCE seq_voting_attribute_templates START 1;
CREATE TABLE voting_attribute_templates
(
  id bigint NOT NULL,
  name character varying(255) NOT NULL,
  value character varying(255) NOT NULL,
  voting_id bigint NOT NULL,
  CONSTRAINT voting_attribute_templates_pkey PRIMARY KEY (id),
  CONSTRAINT fk_fq1se9m7r3yupa5b6gx47qqjy FOREIGN KEY (voting_id)
      REFERENCES voting_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE voting_attribute_templates
  OWNER TO kabinet;

CREATE SEQUENCE seq_voting_item_templates START 1;
CREATE TABLE voting_item_templates
(
  id bigint NOT NULL,
  value character varying(255) NOT NULL,
  voting_id bigint NOT NULL,
  CONSTRAINT voting_item_templates_pkey PRIMARY KEY (id),
  CONSTRAINT fk_1msv8ter1q5jrqaxkgat5xdnm FOREIGN KEY (voting_id)
      REFERENCES voting_templates (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE voting_item_templates
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.



-- // create_table_gcm_push_notification_statuses
-- Migration SQL that makes the change goes here.

CREATE TABLE gcm_push_notification_statuses
(
  id bigint NOT NULL,
  device_id character varying(255) NOT NULL,
  is_pushed boolean NOT NULL,
  chat_message_id bigint,
  notification_id bigint,
  sharer_id bigint NOT NULL,
  CONSTRAINT gcm_push_notification_statuses_pkey PRIMARY KEY (id),
  CONSTRAINT fk_66v92vkb1l2n4m66419lbmo6b FOREIGN KEY (chat_message_id)
      REFERENCES chat_messages (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_edam9825dpb52ux4qnbheb0g5 FOREIGN KEY (sharer_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_oqwtfoywredm9tv8umi5r48s7 FOREIGN KEY (notification_id)
      REFERENCES notifications (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.



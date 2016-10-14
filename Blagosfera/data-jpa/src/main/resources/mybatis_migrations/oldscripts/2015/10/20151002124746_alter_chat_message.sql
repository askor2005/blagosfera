-- // alter_chat_message
-- Migration SQL that makes the change goes here.

ALTER TABLE chat_messages DROP COLUMN read;

CREATE TABLE IF NOT EXISTS chat_message_receivers
(
  id bigint NOT NULL,
  read boolean NOT NULL,
  message_id bigint NOT NULL,
  receiver_id bigint NOT NULL,
  CONSTRAINT chat_message_receivers_pkey PRIMARY KEY (id),
  CONSTRAINT fk_el0e9rf1eb8dlbr468bdfdfro FOREIGN KEY (message_id)
      REFERENCES chat_messages (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_lkjii2mupw1frmvnrce2xbf4k FOREIGN KEY (receiver_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE chat_message_receivers
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.



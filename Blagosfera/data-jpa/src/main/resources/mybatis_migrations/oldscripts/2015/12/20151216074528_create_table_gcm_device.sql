-- // create_table_gcm_device
-- Migration SQL that makes the change goes here.

CREATE TABLE gcm_devices
(
  id bigint NOT NULL,
  device_id character varying(255) NOT NULL,
  sharer_id bigint NOT NULL,
  CONSTRAINT gcm_devices_pkey PRIMARY KEY (id),
  CONSTRAINT fk_2ed8n087omfk7n13qbu09i1a1 FOREIGN KEY (sharer_id)
      REFERENCES sharers (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

-- //@UNDO
-- SQL to undo the change goes here.



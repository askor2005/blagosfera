-- // add_community_security_permissions
-- Migration SQL that makes the change goes here.

CREATE TABLE community_security_permissions_communities
(
  community_permission_id bigint NOT NULL,
  community_id bigint NOT NULL,
  CONSTRAINT community_security_permissions_communities_pkey PRIMARY KEY (community_permission_id, community_id),
  CONSTRAINT fk_1o0liw0fkb4po935317x1phv4 FOREIGN KEY (community_id)
      REFERENCES communities (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_i95d1o0bi1pro3wvkas4ars9y FOREIGN KEY (community_permission_id)
      REFERENCES community_permissions (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE community_security_permissions_communities
  OWNER TO kabinet;

-- //@UNDO
-- SQL to undo the change goes here.



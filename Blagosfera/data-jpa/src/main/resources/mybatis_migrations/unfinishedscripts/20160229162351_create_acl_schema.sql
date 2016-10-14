-- // create acl schema
-- Migration SQL that makes the change goes here.

CREATE TABLE acl_sid (
  id        BIGSERIAL    NOT NULL PRIMARY KEY,
  principal BOOLEAN      NOT NULL,
  sid       VARCHAR(100) NOT NULL,
  CONSTRAINT uk_acl_sid_sid_principal UNIQUE (sid, principal)
);

CREATE TABLE acl_class (
  id    BIGSERIAL    NOT NULL PRIMARY KEY,
  class VARCHAR(100) NOT NULL,
  CONSTRAINT uk_acl_class_class UNIQUE (class)
);

CREATE TABLE acl_object_identity (
  id                 BIGSERIAL PRIMARY KEY,
  object_id_class    BIGINT  NOT NULL,
  object_id_identity BIGINT  NOT NULL,
  parent_object      BIGINT,
  owner_sid          BIGINT,
  entries_inheriting BOOLEAN NOT NULL,
  CONSTRAINT acl_object_identity_unique_1 UNIQUE (object_id_class, object_id_identity),
  CONSTRAINT fk_acl_object_identity_acl_object_identity FOREIGN KEY (parent_object) REFERENCES acl_object_identity (id),
  CONSTRAINT fk_acl_object_identity_acl_class FOREIGN KEY (object_id_class) REFERENCES acl_class (id),
  CONSTRAINT fk_acl_object_identity_acl_sid FOREIGN KEY (owner_sid) REFERENCES acl_sid (id)
);

CREATE TABLE acl_entry (
  id                  BIGSERIAL PRIMARY KEY,
  acl_object_identity BIGINT  NOT NULL,
  ace_order           INT     NOT NULL,
  sid                 BIGINT  NOT NULL,
  mask                INTEGER NOT NULL,
  granting            BOOLEAN NOT NULL,
  audit_success       BOOLEAN NOT NULL,
  audit_failure       BOOLEAN NOT NULL,
  CONSTRAINT uk_acl_entry_acl_object_identity_ace_order UNIQUE (acl_object_identity, ace_order),
  CONSTRAINT fk_acl_entry_acl_object_identity FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity (id),
  CONSTRAINT fk_acl_entry_acl_sid FOREIGN KEY (sid) REFERENCES acl_sid (id)
);

-- //@UNDO
-- SQL to undo the change goes here.



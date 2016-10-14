-- // change_contact_groups_many_to_many
-- Migration SQL that makes the change goes here.
BEGIN;
CREATE TABLE contacts_group_contacts
(
  contact_group_id bigint NOT NULL REFERENCES contacts_groups(id),
  contact_id bigint NOT NULL REFERENCES contacts(id)
);
insert into contacts_group_contacts select contacts_group_id,id from contacts where contacts_group_id is not null;
COMMIT;

-- //@UNDO
-- SQL to undo the change goes here.
drop table contacts_group_contacts;




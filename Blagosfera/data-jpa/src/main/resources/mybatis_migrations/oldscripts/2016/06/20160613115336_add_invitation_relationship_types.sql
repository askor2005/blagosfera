-- // add invitation relationship types
-- Migration SQL that makes the change goes here.

insert into invite_relationship_types (id, name, index)
    values (nextval('seq_invite_relationship_types'), 'однокурсники', 13);

-- //@UNDO
-- SQL to undo the change goes here.



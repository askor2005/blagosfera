-- // add_role_votings_admin
-- Migration SQL that makes the change goes here.

insert into community_permissions(id, name, position, title, description, security_role)
select nextval('seq_community_permissions'), 'VOTINGS_ADMIN', 401, 'Роль просмотра всех собраний объединения', 'Даёт возможность просмтреть все собрания текущего объединения', false
where not exists(select id from community_permissions where name = 'VOTINGS_ADMIN');

-- //@UNDO
-- SQL to undo the change goes here.



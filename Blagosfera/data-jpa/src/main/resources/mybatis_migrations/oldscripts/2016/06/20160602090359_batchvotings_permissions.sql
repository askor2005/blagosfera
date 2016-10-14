-- // batchvotings permissions
-- Migration SQL that makes the change goes here.

INSERT INTO community_permissions (id, name, position, title, description, security_role)
  SELECT
    nextval('seq_community_permissions'),
    'VOTINGS_VIEW',
    401,
    'Просмотр всех собраний объединения',
    'Даёт возможность просмотреть все собрания текущего объединения',
    FALSE
  WHERE NOT exists(SELECT id
                   FROM community_permissions
                   WHERE name = 'VOTINGS_VIEW');

UPDATE community_permissions
SET title     = 'Администрирование собраний',
  description = 'Создание/редактирование собраний и шаблонов собраний объединения'
WHERE name = 'VOTINGS_ADMIN'

-- //@UNDO
-- SQL to undo the change goes here.



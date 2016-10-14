-- // optimization_rev_1
-- Migration SQL that makes the change goes here.

  -- Индексы для проверки и получения ролей пользователя
  CREATE INDEX ON community_permissions(name);
  CREATE INDEX ON community_permissions(id);
  CREATE INDEX ON community_posts_permissions(permission_id);
  CREATE INDEX ON community_posts_permissions(post_id);
  CREATE INDEX ON community_members_posts(post_id);
  CREATE INDEX ON community_members_posts(member_id);
  CREATE INDEX ON community_members(id);
  CREATE INDEX ON community_members(sharer_id);
  CREATE INDEX ON community_permissions(security_role);

-- //@UNDO
-- SQL to undo the change goes here.



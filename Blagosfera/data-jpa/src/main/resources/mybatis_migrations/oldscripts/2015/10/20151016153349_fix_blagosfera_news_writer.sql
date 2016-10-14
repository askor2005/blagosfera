-- // fix blagosfera news writer
-- Migration SQL that makes the change goes here.

do $$
declare permissionId bigint;
declare formId bigint;
declare communityAdminsId bigint;
declare communityPostId bigint;
begin
  select id into formId from community_association_forms where internal_name='BLAGOSFERA_EDITORS';
  select id into communityAdminsId from communities where association_scope_id=formId;

  RAISE NOTICE 'permissionId: %', permissionId;
  RAISE NOTICE 'formId: %', formId;
  RAISE NOTICE 'communityAdminsId: %', communityAdminsId;

  insert into community_permissions (id, name, position, title, description, community_association_form_id, security_role)
  select nextval('seq_community_permissions'), 'BLAGOSFERA_NEWS_WRITER', 100, 'Роль создания новостей', 'Даёт возможность создавать новости', formId, true
  where not exists (select id from community_permissions where name = 'BLAGOSFERA_NEWS_WRITER');

  select id into permissionId from community_permissions where name='BLAGOSFERA_NEWS_WRITER';

  select id into communityPostId from community_posts where community_id=communityAdminsId and name='Редактор';
  insert into community_posts_permissions values(communityPostId,permissionId);

  select id into communityPostId from community_posts where community_id=communityAdminsId and name='Генеральный директор';
  insert into community_posts_permissions values(communityPostId,permissionId);
end $$;

-- //@UNDO
-- SQL to undo the change goes here.



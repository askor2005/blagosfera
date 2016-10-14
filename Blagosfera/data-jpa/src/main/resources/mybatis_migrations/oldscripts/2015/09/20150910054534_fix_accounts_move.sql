-- // fix accounts move
-- Migration SQL that makes the change goes here.

-- Фикс перевода средств с документом.

-- Куда-то пропало с прошлого раза.
-- Создание разрешения(community_permissions) перевода денег
do $$
declare
begin

  insert into community_permissions (id, name, position, title, description, community_association_form_id, security_role)
  select nextval('seq_community_permissions'), 'TRANSFER_MONEY', 54, 'Перевод денег с баланса объединения', 'Даёт возможность совершать перевод денег с баланса объединения', null, false
  where not exists (select 1 from community_permissions where name = 'TRANSFER_MONEY');

end $$;

-- Тоже пропали.
-- Создание классов документов и самих документов для перевода средств для юр.юр, юр.физ и физ.юр
do $$
declare parentId bigint;

declare sharerCommunityClassId bigint;
declare communityCommunityId bigint;

declare sharerCommunityTemplateId bigint;
declare communitySharerTemplateId bigint;
declare communityCommunityTemplateId bigint;
begin

  select id into parentId from documents_types where key = 'UNIONS';


  insert into documents_types (id, name, key, parent_id)
  select nextval('seq_documents_types'), 'Перевод средств между физ. лицами и объединениями', 'SHARER_COMMUNITY_CLASS', parentId
  where not exists (select 1 from documents_types where key = 'SHARER_COMMUNITY_CLASS' and parent_id = parentId);

  insert into documents_types (id, name, key, parent_id)
  select nextval('seq_documents_types'), 'Перевод средств между объединениями', 'COMMUNITY_COMMUNITY_CLASS', parentId
  where not exists (select 1 from documents_types where key = 'COMMUNITY_COMMUNITY_CLASS' and parent_id = parentId);


  select id into sharerCommunityClassId from documents_types where key = 'SHARER_COMMUNITY_CLASS' and parent_id = parentId;
  select id into communityCommunityId from documents_types where key = 'COMMUNITY_COMMUNITY_CLASS' and parent_id = parentId;


  insert into documents_types_participants (id, document_type_id, participant_name, participant_type, association_form_id)
  select nextval('seq_documents_types_participants'), sharerCommunityClassId, 'part1', 'INDIVIDUAL', null
  where not exists (select 1 from documents_types_participants where document_type_id = sharerCommunityClassId and participant_name = 'part1' and participant_type = 'INDIVIDUAL');

  insert into documents_types_participants (id, document_type_id, participant_name, participant_type, association_form_id)
  select nextval('seq_documents_types_participants'), sharerCommunityClassId, 'part2', 'COMMUNITY_WITH_ORGANIZATION', null
  where not exists (select 1 from documents_types_participants where document_type_id = sharerCommunityClassId and participant_name = 'part2' and participant_type = 'COMMUNITY_WITH_ORGANIZATION');


  insert into documents_types_participants (id, document_type_id, participant_name, participant_type, association_form_id)
  select nextval('seq_documents_types_participants'), communityCommunityId, 'part1', 'COMMUNITY_WITH_ORGANIZATION', null
  where not exists (select 1 from documents_types_participants where document_type_id = communityCommunityId and participant_name = 'part1' and participant_type = 'COMMUNITY_WITH_ORGANIZATION');

  insert into documents_types_participants (id, document_type_id, participant_name, participant_type, association_form_id)
  select nextval('seq_documents_types_participants'), communityCommunityId, 'part2', 'COMMUNITY_WITH_ORGANIZATION', null
  where not exists (select 1 from documents_types_participants where document_type_id = communityCommunityId and participant_name = 'part2' and participant_type = 'COMMUNITY_WITH_ORGANIZATION');


  insert into documents_templates (id, content, name, document_type_id, creator_id, document_name, code, help_link)
  select nextval('seq_documents_templates'), '<p>Я,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796119288" data-is-meta-field="false" data-participant-id="349" data-field-id="284" data-internal-name="PERSON_FULL_NAME" data-case-id="CASE_I">[part1:ФИО:Именительный]</span>&nbsp;, даю объединению&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796142684" data-is-meta-field="false" data-participant-id="350" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME">[part2:Полное название на русском языке]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796201348" data-participant-id="349" data-participant-name="part1" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part1:money:money]</span>&nbsp;</p>', 'SharerCommunityAccountsMove', sharerCommunityClassId, 573, 'SharerCommunityAccountsMove', 'SharerCommunityAccountsMove', ''
  where not exists (select 1 from documents_templates where code = 'SharerCommunityAccountsMove');

  insert into documents_templates (id, content, name, document_type_id, creator_id, document_name, code, help_link)
  select nextval('seq_documents_templates'), '<p>Мы,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796261828" data-is-meta-field="false" data-participant-id="350" data-group-internal-name="" data-field-id="274" data-internal-name="COMMUNITY_SHORT_NAME">[part2:Короткое название на русском языке]</span>&nbsp;, даём&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796286223" data-is-meta-field="false" data-participant-id="349" data-group-internal-name="" data-field-id="284" data-internal-name="PERSON_FULL_NAME" data-case-id="CASE_D">[part1:ФИО:Дательный]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796306365" data-participant-id="350" data-participant-name="part2" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part2:money:money]</span>&nbsp;</p>', 'CommunitySharerAccountsMove', sharerCommunityClassId, 573, 'CommunitySharerAccountsMove', 'CommunitySharerAccountsMove', ''
  where not exists (select 1 from documents_templates where code = 'CommunitySharerAccountsMove');

  insert into documents_templates (id, content, name, document_type_id, creator_id, document_name, code, help_link)
  select nextval('seq_documents_templates'), '<p>Мы,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796403384" data-is-meta-field="false" data-participant-id="353" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME">[part1:Полное название на русском языке]</span>&nbsp;, даём объединению&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796424729" data-is-meta-field="false" data-participant-id="354" data-group-internal-name="" data-field-id="274" data-internal-name="COMMUNITY_SHORT_NAME">[part2:Короткое название на русском языке]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796448276" data-participant-id="353" data-participant-name="part1" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part1:money:money]</span>&nbsp;</p>', 'CommunitiesAccountsMove', communityCommunityId, 573, 'CommunitiesAccountsMove', 'CommunitiesAccountsMove', ''
  where not exists (select 1 from documents_templates where code = 'CommunitiesAccountsMove');


  select id into sharerCommunityTemplateId from documents_templates where code = 'SharerCommunityAccountsMove';
  select id into communitySharerTemplateId from documents_templates where code = 'CommunitySharerAccountsMove';
  select id into communityCommunityTemplateId from documents_templates where code = 'CommunitiesAccountsMove';


  insert into documents_template_participants (id, parent_participant_name, participant_name, document_template_id)
  select nextval('seq_documents_template_participants'), '', 'part1', sharerCommunityTemplateId
  where not exists (select 1 from documents_template_participants where parent_participant_name = '' and participant_name = 'part1' and document_template_id = sharerCommunityTemplateId);

  insert into documents_template_participants (id, parent_participant_name, participant_name, document_template_id)
  select nextval('seq_documents_template_participants'), 'part2', 'Руководство', communitySharerTemplateId
  where not exists (select 1 from documents_template_participants where parent_participant_name = 'part2' and participant_name = 'Руководство' and  document_template_id = communitySharerTemplateId);

  insert into documents_template_participants (id, parent_participant_name, participant_name, document_template_id)
  select nextval('seq_documents_template_participants'), 'part1', 'Руководство', communityCommunityTemplateId
  where not exists (select 1 from documents_template_participants where parent_participant_name = 'part1' and participant_name = 'Руководство' and  document_template_id = communityCommunityTemplateId);

  insert into documents_template_participants (id, parent_participant_name, participant_name, document_template_id)
  select nextval('seq_documents_template_participants'), 'part2', 'Руководство', communityCommunityTemplateId
  where not exists (select 1 from documents_template_participants where parent_participant_name = 'part2' and participant_name = 'Руководство' and  document_template_id = communityCommunityTemplateId);

end $$;


-- Фикс полномочий перевода денег для руководителей и генеральных директоров объединений которые уже были созданы ранее
do $$
DECLARE post RECORD;
DECLARE permission RECORD;
begin

  -- цикл по постам руководителей и генеральных директоров, которым будут переназначены все полномочия
  FOR post IN SELECT * FROM community_posts where ceo = true
  LOOP

    -- цикл по полномочиям для объединений
    FOR permission IN SELECT * FROM community_permissions where security_role = false
    LOOP

      insert into community_posts_permissions (post_id, permission_id)
      select post.id, permission.id
      where not exists (select 1 from community_posts_permissions where post_id = post.id and permission_id = permission.id);

    END LOOP;

  END LOOP;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.



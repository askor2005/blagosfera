-- // fix account move russian
-- Migration SQL that makes the change goes here.

-- Фик кракозябр в роли перевода денег
do $$
declare
begin

  UPDATE community_permissions
  SET title='Перевод денег с баланса объединения',description='Даёт возможность совершать перевод денег с баланса объединения'
  WHERE name = 'TRANSFER_MONEY';

end $$;

-- Фикс кракозябр в документах перевода денег
do $$
declare sharerCommunityTemplateId bigint;
declare communitySharerTemplateId bigint;
declare communityCommunityTemplateId bigint;
begin

  UPDATE documents_types
  SET name='Перевод средств между физ. лицами и объединениями'
  WHERE key = 'SHARER_COMMUNITY_CLASS';

  UPDATE documents_types
  SET name='Перевод средств между объединениями'
  WHERE key = 'COMMUNITY_COMMUNITY_CLASS';


  UPDATE documents_templates
  SET content='<p>Я,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796119288" data-is-meta-field="false" data-participant-id="349" data-field-id="284" data-internal-name="PERSON_FULL_NAME" data-case-id="CASE_I">[part1:ФИО:Именительный]</span>&nbsp;, даю объединению&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796142684" data-is-meta-field="false" data-participant-id="350" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME">[part2:Полное название на русском языке]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796201348" data-participant-id="349" data-participant-name="part1" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part1:money:money]</span>&nbsp;</p>'
  WHERE code = 'SharerCommunityAccountsMove';

  UPDATE documents_templates
  SET content='<p>Мы,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796261828" data-is-meta-field="false" data-participant-id="350" data-group-internal-name="" data-field-id="274" data-internal-name="COMMUNITY_SHORT_NAME">[part2:Короткое название на русском языке]</span>&nbsp;, даём&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796286223" data-is-meta-field="false" data-participant-id="349" data-group-internal-name="" data-field-id="284" data-internal-name="PERSON_FULL_NAME" data-case-id="CASE_D">[part1:ФИО:Дательный]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796306365" data-participant-id="350" data-participant-name="part2" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part2:money:money]</span>&nbsp;</p>'
  WHERE code = 'CommunitySharerAccountsMove';

  UPDATE documents_templates
  SET content='<p>Мы,&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796403384" data-is-meta-field="false" data-participant-id="353" data-group-internal-name="" data-field-id="201" data-internal-name="COMMUNITY_NAME">[part1:Полное название на русском языке]</span>&nbsp;, даём объединению&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-filter" data-span-id="1441796424729" data-is-meta-field="false" data-participant-id="354" data-group-internal-name="" data-field-id="274" data-internal-name="COMMUNITY_SHORT_NAME">[part2:Короткое название на русском языке]</span>&nbsp;<span class="mceNonEditable" data-placeholder="" data-span-type="radom-participant-custom-fields" data-span-id="1441796448276" data-participant-id="353" data-participant-name="part1" data-custom-field-type-name="currency" data-custom-field-name="money" data-custom-field-description="money" data-position="1" data-currency-is-words-type="true">[part1:money:money]</span>&nbsp;</p>'
  WHERE code = 'CommunitiesAccountsMove';


  select id into sharerCommunityTemplateId from documents_templates where code = 'SharerCommunityAccountsMove';
  select id into communitySharerTemplateId from documents_templates where code = 'CommunitySharerAccountsMove';
  select id into communityCommunityTemplateId from documents_templates where code = 'CommunitiesAccountsMove';


  UPDATE documents_template_participants
  SET parent_participant_name='',participant_name='part1'
  WHERE parent_participant_name = '' and participant_name = 'part1' and document_template_id = sharerCommunityTemplateId;

  UPDATE documents_template_participants
  SET parent_participant_name='part2',participant_name='Руководство'
  WHERE parent_participant_name = 'part2' and participant_name = 'Р СѓРєРѕРІРѕРґСЃС‚РІРѕ' and document_template_id = communitySharerTemplateId;

  UPDATE documents_template_participants
  SET parent_participant_name='part1',participant_name='Руководство'
  WHERE parent_participant_name = 'part1' and participant_name = 'Р СѓРєРѕРІРѕРґСЃС‚РІРѕ' and document_template_id = communityCommunityTemplateId;

  UPDATE documents_template_participants
  SET parent_participant_name='part2',participant_name='Руководство'
  WHERE parent_participant_name = 'part2' and participant_name = 'Р СѓРєРѕРІРѕРґСЃС‚РІРѕ' and document_template_id = communityCommunityTemplateId;

end $$;


-- //@UNDO
-- SQL to undo the change goes here.



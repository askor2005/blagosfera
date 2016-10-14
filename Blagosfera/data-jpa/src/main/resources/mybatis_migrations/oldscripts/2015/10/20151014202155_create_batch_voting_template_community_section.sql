-- // create_batch_voting_template_community_section
-- Migration SQL that makes the change goes here.

do $$
declare
sectionId bigint;
associactionForm bigint;
begin
  select id into sectionId from community_sections where name = 'COMMUNITY_VOTINGS' and parent_id is null;

  select id into associactionForm from list_editor_item where mnemo_code = 'community_cooperative_society';

  insert into community_sections (id, link, name, permission, position, title, parent_id, asoociation_form_id, is_guest_access)
  select nextval('seq_community_sections'), '/batchVotingTemplates.html', 'BATCH_VOTING_TEMPLATES', null, 3, 'Шаблоны собраний', sectionId, associactionForm, false
  where not exists(select id from community_sections where name = 'BATCH_VOTING_TEMPLATES' and parent_id = sectionId);

  insert into community_sections (id, link, name, permission, position, title, parent_id, asoociation_form_id, is_guest_access)
  select nextval('seq_community_sections'), '/batchVotingConstructor.html', 'BATCH_VOTING_CONSTRUCTOR', null, 4, 'Конструктор собраний', sectionId, associactionForm, false
  where not exists(select id from community_sections where name = 'BATCH_VOTING_CONSTRUCTOR' and parent_id = sectionId);

end $$;

-- //@UNDO
-- SQL to undo the change goes here.



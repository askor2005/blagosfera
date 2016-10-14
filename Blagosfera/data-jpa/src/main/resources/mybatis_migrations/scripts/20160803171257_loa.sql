-- // loa
-- Migration SQL that makes the change goes here.

DELETE FROM documents_template_participants
WHERE participant_name = 'Кандидат в пайщики физ. лицо'
      AND
      document_template_id = (SELECT documents_templates.id
                              FROM documents_templates
                              WHERE documents_templates.code =
                                    'protocol_join_new_members_to_cooperative_loa');

-- //@UNDO
-- SQL to undo the change goes here.



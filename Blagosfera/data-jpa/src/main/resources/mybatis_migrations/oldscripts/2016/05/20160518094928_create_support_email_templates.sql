-- // create_support_email_templates
-- Migration SQL that makes the change goes here.
do $$
begin
INSERT INTO documents_templates (id, content, name, document_type_id,creator_id, code,document_name,document_short_name,position)
  SELECT nextval('seq_documents_templates'), '', 'Уведомление по почте. Обратная связь', 2827,485, 'email.notify.support-request-accepted','<p>Обратная связь</p>',
  '<p>Обратная связь</p>',0
  WHERE NOT EXISTS (SELECT code FROM documents_templates WHERE code='email.notify.support-request-accepted');

update documents_templates
  set content = '<table border="0" cellspacing="0" cellpadding="0" width="100%" style="font-family: arial, sans-serif; width: 100%; color: #616266;">
                <tr>
                    <td width="100%">
                          Ваш вопрос по теме: "${theme}" был принят. Мы свяжемся с вами в ближайшее время.
                                        <p style="font-family:Arial; font-size:13px; padding: 5px 10px; margin: 5px 0; color: #333333;"><a href="${applicationUrl}/login">Вход в Систему</a></p>
                    </td>
                </tr>
            </table>'
 where code='email.notify.support-request-accepted';

INSERT INTO documents_templates (id, content, name, document_type_id,creator_id, code,document_name,document_short_name,position)
  SELECT nextval('seq_documents_templates'), '', 'Уведомление по почте. Обратная связь - для админов', 2827,485, 'email.notify.support-request-created','<p>Новый запрос по обратной связи от пользователей</p>',
  '<p>Запрос по обратной связи принят</p>',0
  WHERE NOT EXISTS (SELECT code FROM documents_templates WHERE code='email.notify.support-request-created');

update documents_templates
  set content = '<table border="0" cellspacing="0" cellpadding="0" width="100%" style="font-family: arial, sans-serif; width: 100%; color: #616266;">
                <tr>
                    <td width="100%">
                            Вам поступил вопрос по теме: "${theme}". Вы можете просмотреть его <a href="${applicationUrl}/ng/#/admin/support/requests">по ссылке</a>.
                                        <p style="font-family:Arial; font-size:13px; padding: 5px 10px; margin: 5px 0; color: #333333;"><a href="${applicationUrl}/login">Вход в Систему</a></p>
                    </td>
                </tr>
            </table>'
 where code='email.notify.support-request-created';
 end $$;

-- //@UNDO
-- SQL to undo the change goes here.
do $$
begin
delete from documents_templates where code='email.notify.support-request-accepted';
	delete from documents_templates where code='email.notify.support-request-created';
 end $$;

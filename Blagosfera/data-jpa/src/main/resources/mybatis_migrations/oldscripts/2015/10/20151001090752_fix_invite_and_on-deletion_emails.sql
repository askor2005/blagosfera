-- // fix invite and on-deletion emails
-- Migration SQL that makes the change goes here.

-- Правки для шаблона приглашения email
do $$
declare n record;
declare fixed_text text;
begin

  select * into n from email_templates where title='invite.email.on-invite';
  if n IS NOT NULL
  then

    fixed_text := replace(n.body, 'приглашает вас в систему', 'приглашает Вас в Систему');
    fixed_text := replace(fixed_text, 'Благосфера', 'БЛАГОСФЕРА');
    UPDATE email_templates SET body=fixed_text WHERE id=n.id;

    fixed_text := 'Система БЛАГОСФЕРА';
    UPDATE email_templates SET send_from=fixed_text WHERE id=n.id;

    fixed_text := 'Приглашение в Систему БЛАГОСФЕРА';
    UPDATE email_templates SET subject=fixed_text WHERE id=n.id;

  end if;

end $$;

-- Правки для шаблона о необходимости заполнить профиль email
do $$
declare n record;
declare fixed_text text;
begin

  select * into n from email_templates where title='notify.email.on-deletion-notification';
  if n IS NOT NULL
  then

    fixed_text := replace(n.body, 'Ваш профиль в системе R@MERA', 'Ваш профиль в Системе БЛАГОСФЕРА');
    UPDATE email_templates SET body=fixed_text WHERE id=n.id;

    fixed_text := 'Система БЛАГОСФЕРА';
    UPDATE email_templates SET send_from=fixed_text WHERE id=n.id;

  end if;

end $$;

-- //@UNDO
-- SQL to undo the change goes here.



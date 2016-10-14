-- // create invitations
-- Migration SQL that makes the change goes here.

DO $$
DECLARE u RECORD;
BEGIN
  FOR u IN SELECT *
           FROM sharers
           WHERE NOT EXISTS(
               SELECT i.*
               FROM invites i
               WHERE i.invited_sharer_id = sharers.id
                     AND ((i.status = 1) OR (i.status = 0))
           )
           ORDER BY id
  LOOP

    insert into invites (
      id, creation_date, invited_email, guarantee, how_long_familiar,
      invited_father_name, invited_first_name, invited_gender, invited_last_name, sharer_id,
      hash_url, status, expire_date, last_sending, invited_sharer_id,
      invites_count)
      select nextval('seq_invites'), u.registered_at, u.email, FALSE, 0,
      '', '', '', '', 143,
      '', 1, u.registered_at, u.registered_at, u.id,
      1;

  END LOOP;
END $$;

-- //@UNDO
-- SQL to undo the change goes here.



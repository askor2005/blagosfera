-- // add sharers to news_subscribes
-- Migration SQL that makes the change goes here.

DO $$
DECLARE
  sharer sharers%ROWTYPE;
  new_id INTEGER;
BEGIN
  FOR sharer IN SELECT * FROM sharers where deleted = FALSE ORDER BY id ASC LOOP

    new_id = nextval('seq_news_subscribes');
    RAISE NOTICE 'processing sharer id = %, subscribe id = %', sharer.id, new_id;

    INSERT INTO news_subscribes (id, sharer_id, scope_type, scope_id)
       (SELECT new_id, sharer.id, 'SHARER', sharer.id
         WHERE NOT EXISTS (SELECT ns.sharer_id FROM news_subscribes ns WHERE ns.sharer_id = sharer.id and ns.scope_id = sharer.id));

  END LOOP;
END $$;

-- //@UNDO
-- SQL to undo the change goes here.



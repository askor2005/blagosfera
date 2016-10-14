-- // create user menu items
-- Migration SQL that makes the change goes here.

SELECT nextval('nav_menu_item_id');

--                                id,                           parent_id, title,             icon,                     path,                  expandable, collapsed, switch_menu, lazy_load, visible, type
INSERT INTO nav_menu_item VALUES (nextval('nav_menu_item_id'),  NULL,      'PROFILE',         'account_circle',         '/user/profile/{ikp}', FALSE,      FALSE,     FALSE,       FALSE,     FALSE,   'USER');
INSERT INTO nav_menu_item VALUES (nextval('nav_menu_item_id'),  NULL,      'SETTINGS',        'settings',               '/user/settings',      FALSE,      FALSE,     FALSE,       FALSE,     TRUE,    'USER');
INSERT INTO nav_menu_item VALUES (nextval('nav_menu_item_id'),  NULL,      'DOCUMENTS',       'folder_open',            '/user/documents',     FALSE,      FALSE,     FALSE,       FALSE,     FALSE,   'USER');
INSERT INTO nav_menu_item VALUES (nextval('nav_menu_item_id'),  NULL,      'ACCOUNT_BALANCE', 'account_balance_wallet', '/account',            FALSE,      FALSE,     FALSE,       FALSE,     TRUE,    'USER');
INSERT INTO nav_menu_item VALUES (nextval('nav_menu_item_id'),  NULL,      'INVITATIONS',     'insert_invitation',      '/invites',            FALSE,      FALSE,     FALSE,       FALSE,     TRUE,    'USER');

-- //@UNDO
-- SQL to undo the change goes here.



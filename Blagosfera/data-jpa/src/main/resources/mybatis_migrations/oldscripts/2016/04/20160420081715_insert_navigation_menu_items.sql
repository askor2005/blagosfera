-- // insert navigation menu items
-- Migration SQL that makes the change goes here.

--                                id  parent_id default_route title                                    icon       path                       expandable collapsed switch_menu lazy_load
INSERT INTO nav_menu_item VALUES (1,  NULL,     FALSE,        'БЛАГОСФЕРА',                            'help',    NULL,                      TRUE,      FALSE,    FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (2,  1,        FALSE,        'О БЛАГОСФЕРЕ',                          'help',    NULL,                      TRUE,      FALSE,    FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (3,  1,        FALSE,        'Правила и руководства Системы',         'help',    NULL,                      TRUE,      FALSE,    FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (4,  1,        FALSE,        'Наши контакты',                         'help',    '/p/55',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (5,  1,        FALSE,        'Наши партнеры',                         'help',    '/p/10',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (6,  NULL,     FALSE,        'Деловой портал',                        'help',    null,                      FALSE,     TRUE,     TRUE,       FALSE);
INSERT INTO nav_menu_item VALUES (7,  6,        FALSE,        'Экономический советник',                'help',    '/ecoadvisor/communities', TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (8,  NULL,     FALSE,        'Администрирование',                     'settings', null,                     FALSE,     TRUE,     TRUE,       FALSE);

INSERT INTO nav_menu_item VALUES (9,  2,        FALSE,        'Определение БЛАГОСФЕРЫ',                'help',    '/p/76',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (10, 2,        FALSE,        'Философия БЛАГОСФЕРЫ',                  'help',    '/p/50',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (11, 2,        FALSE,        'Цели, задачи и намерения БЛАГОСФЕРЫ',   'help',    '/p/52',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (12, 2,        FALSE,        'Как организована БЛАГОСФЕРА',           'help',    '/p/721',                  TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (13, 2,        FALSE,        'Возможности Системы БЛАГОСФЕРА',        'help',    '/p/51',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (14, 2,        FALSE,        'Потребительские Общества в БЛАГОСФЕРЕ', 'help',    '/p/78',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (15, 2,        FALSE,        'Как организована Безопасность?',        'lock',    '/p/85',                   TRUE,      TRUE,     FALSE,      FALSE);

INSERT INTO nav_menu_item VALUES (16, 3,        FALSE,        'Правила Системы БЛАГОСФЕРА',            'help',    '/p/56',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (17, 3,        FALSE,        'Руководство пользователя Системы',      'help',    '/p/12',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (18, 3,        FALSE,        'Руководство Регистратора Системы',      'help',    '/p/57',                   TRUE,      TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (19, 3,        FALSE,        'Руководство по созданию и сертификации Объединений в Системе', 'help', '/p/58', TRUE,    TRUE,     FALSE,      FALSE);
INSERT INTO nav_menu_item VALUES (20, 3,        FALSE,        'Тарифный план Регистраторов и их помощников', 'help', '/p/59',                TRUE,      TRUE,     FALSE,      FALSE);

-- //@UNDO
-- SQL to undo the change goes here.



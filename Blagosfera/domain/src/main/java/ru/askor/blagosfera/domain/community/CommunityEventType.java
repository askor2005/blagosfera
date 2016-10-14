package ru.askor.blagosfera.domain.community;

public enum CommunityEventType {

    INVITE {
        @Override
        public String toString() {
            return "Отправка приглашения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    ACCEPT_REQUEST {
        @Override
        public String toString() {
            return "Одобрение запроса на вступление";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    REJECT_REQUEST {
        @Override
        public String toString() {
            return "Отклонение запроса на вступление";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    EXCLUDE {
        @Override
        public String toString() {
            return "Исключение из объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CANCEL_INVITE {
        @Override
        public String toString() {
            return "Отмена приглашения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    JOIN {
        @Override
        public String toString() {
            return "Вход в объединение";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    REQUEST {
        @Override
        public String toString() {
            return "Подача запроса на вступление";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CONDITION_NOT_DONE_REQUEST {
        @Override
        public String toString() {
            return "Подача запроса на вступление в объединение с выполнением условия";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CONDITION_DONE_REQUEST {
        @Override
        public String toString() {
            return "Условие на вступление в объединение выполнено. Ожидается решение руководства объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CANCEL_REQUEST {
        @Override
        public String toString() {
            return "Отмена заявки на вступление";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    ACCEPT_INVITE {
        @Override
        public String toString() {
            return "Принятие приглашения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    REJECT_INVITE {
        @Override
        public String toString() {
            return "Отклонение приглашения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    LEAVE {
        @Override
        public String toString() {
            return "Выход из объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CREATE_NEWS {
        @Override
        public String toString() {
            return "Создание новости";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.NEWS;
        }
    },
    EDIT_NEWS {
        @Override
        public String toString() {
            return "Редактирование новости";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.NEWS;
        }
    },
    DELETE_NEWS {
        @Override
        public String toString() {
            return "Удаление новости";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.NEWS;
        }
    },
    ADD_MEMBER {
        @Override
        public String toString() {
            return "Добавление участника";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },
    CANCEL_REQUEST_LEAVE {
        @Override
        public String toString() {
            return "Отмена заявки на выход из объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    CREATED {
        @Override
        public String toString() {
            return "Создано объединение";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.COMMON;
        }

    },
    DELETED {
        @Override
        public String toString() {
            return "Удаление";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.COMMON;
        }

    },

    APPOINT_BY_VOTING {
        @Override
        public String toString() {
            return "Назначение на должность по итогам голосования";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    APPOINT {
        @Override
        public String toString() {
            return "Назначение на должность";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    DISAPPOINT {
        @Override
        public String toString() {
            return "Снятие с должности";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    REQUEST_TO_LEAVE {
        @Override
        public String toString() {
            return "Запрос на выход из объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    LEAVE_IN_PROCESS {
        @Override
        public String toString() {
            return "Выход из объединения в рассмотрении";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    REQUEST_TO_APPOINT_POST {
        @Override
        public String toString() {
            return "Запрос на назначение в должность";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    MEMBER_VOTING_POST {
        @Override
        public String toString() {
            return "Выбран участник на должность";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    NEED_APPOINT_MEMBER_TO_POST {
        @Override
        public String toString() {
            return "Необходимо назначить участника на должность";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    ACCEPT_REQUEST_TO_COOPERATIVE {
        @Override
        public String toString() {
            return "Одобрение запроса на вступление в Потребительское Общество";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    LEAVE_FROM_COOPERATIVE_IS_DONE {
        @Override
        public String toString() {
            return "Выход из Потребительского Общества выполнен";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.MEMBERS;
        }
    },

    CREATE_FILE {
        @Override
        public String toString() {
            return "Создан файл объединения";
        }

        @Override
        public CommunityEventTypeGroup getGroup() {
            return CommunityEventTypeGroup.COMMON;
        }
    };

    public abstract CommunityEventTypeGroup getGroup();

}

package ru.askor.blagosfera.domain.community;

public enum CommunityEventTypeGroup {

    COMMON {
        @Override
        public String toString() {
            return "Общее";
        }
    },

    NEWS {
        @Override
        public String toString() {
            return "Новости";
        }
    },

    MEMBERS {
        @Override
        public String toString() {
            return "Участники";
        }
    }

}

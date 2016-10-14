package ru.radom.kabinet.model.cyberbrain;

public enum ScoreObjects {
    CREATE_TASK("CREATE_TASK"),
    USER_TASK_CHANGE_STATUS_READY("USER_TASK_CHANGE_STATUS_READY"),
    ANSWER_QUESTION_IN_TASKS("ANSWER_QUESTION_IN_TASKS"),
    INDICATION_OF_THE_PROBLEM("INDICATION_OF_THE_PROBLEM"),
    JOURNAL_ATTENTION_NEW_RECORD("JOURNAL_ATTENTION_NEW_RECORD"),
    THESAURUS_NEW_RECORD("THESAURUS_NEW_RECORD"),
    THESAURUS_CREATE_SINONIM("THESAURUS_CREATE_SINONIM"),
    KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_THIS("KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_THIS"),
    KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_MANY("KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_MANY"),
    KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_PROPERTY("KNOWLEDGE_REPOSITORY_ANSWER_QUESTION_PROPERTY");

    private final String name;

    ScoreObjects(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
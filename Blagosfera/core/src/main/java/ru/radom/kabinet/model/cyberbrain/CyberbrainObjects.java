package ru.radom.kabinet.model.cyberbrain;

public enum CyberbrainObjects {
    JOURNAL_ATTENTION("JOURNAL_ATTENTION"),
    THESAURUS("THESAURUS"),
    KNOWLEDGE_REPOSITORY("KNOWLEDGE_REPOSITORY"),
    KNOWLEDGE_REPOSITORY_CONDITION("KNOWLEDGE_REPOSITORY_CONDITION"),
    USER_TASK("USER_TASK"),
    USER_PROBLEM("USER_PROBLEM"),
    FILE("FILE"),
    USER("USER");

    private final String name;

    CyberbrainObjects(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
package ru.radom.kabinet.model.utils;

/**
 * Created by ebelyaev on 15.10.2015.
 */
// вспомогательный класс-результат определения схожести адресов
// matchesCount количество совпавших полей
// признак того, что все определённые поля совпадают
public class Similarities {
    private int matchesCount;
    private boolean allMatches;

    public Similarities(int matchesCount, boolean allMatches) {
        this.matchesCount = matchesCount;
        this.allMatches = allMatches;
    }

    public int getMatchesCount() {
        return matchesCount;
    }

    public void setMatchesCount(int matchesCount) {
        this.matchesCount = matchesCount;
    }

    public boolean isAllMatches() {
        return allMatches;
    }

    public void setAllMatches(boolean allMatches) {
        this.allMatches = allMatches;
    }
}

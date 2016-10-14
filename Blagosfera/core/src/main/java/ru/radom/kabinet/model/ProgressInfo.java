package ru.radom.kabinet.model;

public class ProgressInfo {
    private ProgressStatus progressStatus;
    private int progressPercent;
    private String progressName;

    public ProgressInfo(ProgressStatus progressStatus, int progressPercent, String progressName) {
        this.progressStatus = progressStatus;
        this.progressPercent = progressPercent;
        this.progressName = progressName;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getProgressName() {
        return progressName;
    }

    public void setProgressName(String progressName) {
        this.progressName = progressName;
    }
}

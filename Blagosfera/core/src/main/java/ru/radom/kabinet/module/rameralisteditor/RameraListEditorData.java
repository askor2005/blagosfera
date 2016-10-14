package ru.radom.kabinet.module.rameralisteditor;

/**
 * Created by vgusev on 06.06.2015.
 */
public class RameraListEditorData {

    private boolean isUserSuperAdmin = false;

    private Object result;

    public boolean isUserSuperAdmin() {
        return isUserSuperAdmin;
    }

    public void setIsUserSuperAdmin(boolean isUserSuperAdmin) {
        this.isUserSuperAdmin = isUserSuperAdmin;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}

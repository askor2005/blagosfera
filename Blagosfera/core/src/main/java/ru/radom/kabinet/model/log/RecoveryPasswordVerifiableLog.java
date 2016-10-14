package ru.radom.kabinet.model.log;

import ru.askor.blagosfera.domain.events.user.SharerEvent;
import ru.askor.blagosfera.domain.events.user.SharerEventType;
import ru.askor.blagosfera.domain.user.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Лог попыток восстановления пароля
 *
 * Created by ebelyaev on 13.08.2015.
 */
@Entity
@Table(name = "recovery_password_verifiable_logs")
public class RecoveryPasswordVerifiableLog extends VerifiableLog {

    @Column(name = "sharer_id", nullable = false)
    private Long sharerId;

    @Column(name = "sharer_ikp",length = 20)
    private String sharerIkp;

    @Column(name = "sharer_email",length = 100)
    private String sharerEmail;

    @Column(name = "sharer_name",length = 1000)
    private String sharerName;

    @Column(name = "comment",length = 1000)
    private String comment;

    public RecoveryPasswordVerifiableLog() {
    }

    private void setSharer(User user) {
        this.sharerId = user.getId();
        this.sharerIkp = user.getIkp();
        this.sharerEmail = user.getEmail();
        this.sharerName = user.getFullName();
    }

    public RecoveryPasswordVerifiableLog(SharerEvent sharerEvent) {
        if (sharerEvent.getType().equals(SharerEventType.RECOVERY_PASSWORD_INIT)) {
            this.comment = "RECOVERY_PASSWORD_INIT";
            setSharer(sharerEvent.getUser());
        } else if (sharerEvent.getType().equals(SharerEventType.RECOVERY_PASSWORD_COMPLETE)) {
            this.comment = "RECOVERY_PASSWORD_COMPLETE";
            setSharer(sharerEvent.getUser());
        }
    }

    @Override
    public String getStringFromFields() {
        return "RecoveryPasswordVerifiableLog" + sharerId + sharerIkp + sharerEmail + sharerName + comment;
    }

    public Long getSharerId() {
        return sharerId;
    }

    public void setSharerId(Long sharerId) {
        this.sharerId = sharerId;
    }

    public String getSharerIkp() {
        return sharerIkp;
    }

    public void setSharerIkp(String sharerIkp) {
        this.sharerIkp = sharerIkp;
    }

    public String getSharerEmail() {
        return sharerEmail;
    }

    public void setSharerEmail(String sharerEmail) {
        this.sharerEmail = sharerEmail;
    }

    public String getSharerName() {
        return sharerName;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

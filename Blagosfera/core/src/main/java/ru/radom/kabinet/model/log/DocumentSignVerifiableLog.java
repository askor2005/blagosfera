package ru.radom.kabinet.model.log;

import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.events.document.RameraFlowOfDocumentEvent;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.document.model.DocumentEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Лог подписи документов
 *
 * Created by ebelyaev on 13.08.2015.
 */
@Entity
@Table(name = "document_sign_verifiable_logs")
public class DocumentSignVerifiableLog extends VerifiableLog {

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_name", length = 1000)
    private String documentName;

    @Column(name = "document_code", length = 100)
    private String documentCode;


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

    public DocumentSignVerifiableLog() {
    }

    private void setDocument(Document document) {
        this.documentId = document.getId();
        this.documentName = document.getName();
        this.documentCode = document.getCode();
    }

    private void setSharer(User user) {
        this.sharerId = user.getId();
        this.sharerIkp = user.getIkp();
        this.sharerEmail = user.getEmail();
        this.sharerName = user.getFullName();
    }

    public DocumentSignVerifiableLog(RameraFlowOfDocumentEvent event) {
        setDocument(event.getDocument());
        setSharer(event.getUser());
        this.comment = "SIGN_DOCUMENT";
    }

    @Override
    public String getStringFromFields() {
        return "DocumentSignVerifiableLog" + documentId + documentName + documentCode + sharerId + sharerIkp + sharerEmail + sharerName + comment;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public void setDocumentCode(String documentCode) {
        this.documentCode = documentCode;
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

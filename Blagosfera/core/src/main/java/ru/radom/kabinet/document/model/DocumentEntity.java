package ru.radom.kabinet.document.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentCreator;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.utils.CommonConstants;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by vgusev on 14.06.2015.
 * Класс - сушность Документ
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "flowOfDocument")
public class DocumentEntity extends LongIdentifiable {

    // Ид системного создателя документа
    public static final long SYSTEM_CREATOR_ID = -1;

    /**
     * Уникальный код документа
     */
    @Column(name = "code", length = 100, unique = true)
    private String code;

    /**
     * Дата создания документа.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", nullable = false)
    private Date createDate;

    /**
     * Полное имя документа.
     */
    @Column(name = "name", nullable = false, length = 10000)
    private String name;

    /**
     * Короткое имя документа.
     */
    @Column(name = "short_name", length = 10000)
    private String shortName;

    /**
     * Контент документа.
     */
    @JsonIgnore
    @Column(name = "content", nullable = false, length = 10000000)
    private String content;

    /**
     * ИД класса документов.
     */
    @Column(name = "class_id")
    private Long documentClassId;

    /**
     * Ссылка на список участников документа.
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "document")
    private List<DocumentParticipantEntity> participants = new ArrayList<>();

    /**
     * ИД создателя документа.
     */
    @Column(name = "creator_id")
    private Long creatorId;

    /**
     * Список параметров документа.
     */
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "document")
    private List<DocumentParameterEntity> parameters = new ArrayList<>();

    /**
     * Уникальный код документа для ссылок.
     */
    @Column(name = "hash_code", unique = true)
    private String hashCode;

    /**
     * Активность документа
     */
    @Column(name = "active", nullable = true)
    private Boolean active;

    /**
     * Дата истечения документа
     */
    @Column(name = "expired_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredDate;

    /**
     * Хеш код документа для его подписания участниками, которые его подписывают
     */
    @JsonIgnore
    @Column(name = "hash_code_for_signature", nullable = true, length = 500)
    private String hashCodeForSignature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private DocumentFolderEntity folder;

    @Column(name = "can_unsign_document", nullable = true)
    private Boolean canUnsignDocument;

    /**
     * Нужно ли подписывать при помощи ЭЦП
     */
    @Column(name = "need_sign_by_eds", nullable = true)
    private Boolean needSignByEDS;

    @Column(name = "pdf_export_arguments")
    private String pdfExportArguments;

    public String getLink() {
        String docId;
        docId = hashCode == null ? String.valueOf(this.getId()) : hashCode;
        return getLink(docId);
    }

    /**
     * Ссылка на pdf
     * @return Ссылка в виде строки
     */
    public String getPdfLink() {
        return CommonConstants.BASE_DOCUMENT_PDF_LINK + getId();
    }

    /**
     * Ссылка на документ
     * @param documentId ИД, либо hashCode
     * @return Строка ссылки на документ
     */
    public static String getLink(String documentId){
        return CommonConstants.BASE_DOCUMENT_LINK + documentId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDocumentClassId() {
        return documentClassId;
    }

    public void setDocumentClassId(Long documentClassId) {
        this.documentClassId = documentClassId;
    }

    public List<DocumentParticipantEntity> getParticipants() {
        return participants;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public List<DocumentParameterEntity> getParameters() {
        return parameters;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getHashCodeForSignature() {
        return hashCodeForSignature;
    }

    public void setHashCodeForSignature(String hashCodeForSignature) {
        this.hashCodeForSignature = hashCodeForSignature;
    }

    public DocumentFolderEntity getFolder() {
        return folder;
    }

    public void setFolder(DocumentFolderEntity folder) {
        this.folder = folder;
    }

    public Boolean getCanUnsignDocument() {
        return canUnsignDocument;
    }

    public void setCanUnsignDocument(Boolean canUnsignDocument) {
        this.canUnsignDocument = canUnsignDocument;
    }

    public Boolean getNeedSignByEDS() {
        return needSignByEDS;
    }

    public void setNeedSignByEDS(Boolean needSignByEDS) {
        this.needSignByEDS = needSignByEDS;
    }

    public String getPdfExportArguments() {
        return pdfExportArguments;
    }

    public void setPdfExportArguments(String pdfExportArguments) {
        this.pdfExportArguments = pdfExportArguments;
    }

    public Document toDomain(boolean withParticipants, DocumentCreator documentCreator) {
        Document document = new Document();
        document.setId(getId());
        document.setCode(getCode());
        document.setCreateDate(getCreateDate());
        document.setName(getName());
        document.setShortName(getShortName());
        document.setContent(getContent());
        document.setDocumentClassId(getDocumentClassId());

        if (withParticipants && (getParticipants().size() > 0)) {
            document.setParticipants(DocumentParticipantEntity.toDomainList(getParticipants(), false, true));
        }
        document.setParameters(DocumentParameterEntity.toDomainList(getParameters()));

        if (documentCreator == null && getCreatorId() != null && getCreatorId() > 0) {
            // TODO Надо как то нормально сделать
            User userCreator = new User();
            userCreator.setId(getCreatorId());
            documentCreator = userCreator;
        }
        document.setCreator(documentCreator);
        document.setHashCode(getHashCode());
        document.setActive(BooleanUtils.toBooleanDefaultIfNull(getActive(), false));
        document.setExpiredDate(getExpiredDate());
        document.setHashCodeForSignature(getHashCodeForSignature());
        document.setLink(getLink());
        // По умолчанию можно неподписать документ
        document.setCanUnsignDocument(BooleanUtils.toBooleanDefaultIfNull(getCanUnsignDocument(), true));
        document.setNeedSignByEDS(BooleanUtils.toBooleanDefaultIfNull(getNeedSignByEDS(), true));
        document.setPdfExportArguments(getPdfExportArguments());
        if (getFolder() != null) {
            document.setDocumentFolder(getFolder().toDomain(false, false));
        }
        return document;
    }

    public static Document toDomainSafe(DocumentEntity entity, boolean withParticipants, DocumentCreator documentCreator) {
        Document result = null;
        if (entity != null) {
            result = entity.toDomain(withParticipants, documentCreator);
        }
        return result;
    }

    public static List<Document> toDomainList(List<DocumentEntity> entities, boolean withParticipants, Map<Long, DocumentCreator> longDocumentCreatorMap) {
        List<Document> result = null;
        if (entities != null) {
            result = new ArrayList<>();
            for (DocumentEntity entity : entities) {
                DocumentCreator documentCreator = null;
                if (longDocumentCreatorMap != null && longDocumentCreatorMap.containsKey(entity.getId())) {
                    documentCreator = longDocumentCreatorMap.get(entity.getId());
                }

                result.add(toDomainSafe(entity, withParticipants, documentCreator));
            }
        }
        return result;
    }
}

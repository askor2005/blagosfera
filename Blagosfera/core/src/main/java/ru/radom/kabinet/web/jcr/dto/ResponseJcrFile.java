package ru.radom.kabinet.web.jcr.dto;

import lombok.Data;

/**
 *
 * Created by vgusev on 13.11.2015.
 */
@Data
public class ResponseJcrFile {

    // Имя файла
    private String fileName;
    // Ссылка на файл
    private String link;
    // ИД файла
    private String nodeIdentifire;
    // Флаг - файл является директорией
    private boolean isFolder;
    // Дата модификации файла
    private Long modifiedTimeStamp;
    // Mime тип файла
    private String mimeType;
    // Размер файла
    private Long fileSize;
    // ИД родительского каталога
    private String parentNodeIdentifire;

    public ResponseJcrFile(String fileName, String nodeIdentifire, Long modifiedTimeStamp, String mimeType, Long fileSize, String parentNodeIdentifire, boolean isFolder) {
        this.fileName = fileName;
        this.nodeIdentifire = nodeIdentifire;
        this.link = "/files/" + nodeIdentifire;
        this.isFolder = isFolder;
        this.modifiedTimeStamp = modifiedTimeStamp;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.parentNodeIdentifire = parentNodeIdentifire;
    }

    public ResponseJcrFile(String fileName, String nodeIdentifire, Long modifiedTimeStamp, String mimeType, Long fileSize, String parentNodeIdentifire) {
        this(fileName, nodeIdentifire, modifiedTimeStamp, mimeType, fileSize, parentNodeIdentifire, false);
    }
}

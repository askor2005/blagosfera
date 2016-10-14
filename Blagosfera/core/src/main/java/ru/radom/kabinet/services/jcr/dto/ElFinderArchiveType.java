package ru.radom.kabinet.services.jcr.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 29.02.2016.
 */
@Getter
public enum ElFinderArchiveType {

    ZIP("application/zip", "zip");

    private String type;

    private String extension;

    ElFinderArchiveType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }
}

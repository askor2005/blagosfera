package ru.radom.kabinet.model.communities.postappointbehavior.impl;

import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.document.Document;
import ru.askor.blagosfera.domain.document.DocumentFolder;

import java.util.List;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
@Data
public class PostAppointData {

    private Community community;

    private PostAppointResultType type;

    private DocumentFolder documentFolder;

    public PostAppointData(Community community, PostAppointResultType type, DocumentFolder documentFolder) {
        this.community = community;
        this.type = type;
        this.documentFolder = documentFolder;
    }

    public PostAppointData(Community community, PostAppointResultType type) {
        this.community = community;
        this.type = type;
    }

}

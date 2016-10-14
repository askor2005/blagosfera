package ru.askor.blagosfera.domain.community;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 22.07.2016.
 */
@Data
public class CommunityDocumentRequestsPage {

    private long count;

    private List<CommunityDocumentRequest> communityDocumentRequests;
}

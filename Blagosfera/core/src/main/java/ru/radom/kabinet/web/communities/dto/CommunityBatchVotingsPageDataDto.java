package ru.radom.kabinet.web.communities.dto;

import lombok.Getter;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Getter
public class CommunityBatchVotingsPageDataDto {

    private boolean votingsAdmin;

    public CommunityBatchVotingsPageDataDto(boolean votingsViewer) {
        this.votingsAdmin = votingsViewer;
    }
}

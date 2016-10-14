package ru.radom.kabinet.services.bpmhandlers.voting.dto;

import java.util.List;

/**
 *
 * Created by vgusev on 10.12.2015.
 */
public class BPMVotingsDto {

    private List<BPMVotingDto> data;

    public List<BPMVotingDto> getData() {
        return data;
    }

    public void setData(List<BPMVotingDto> data) {
        this.data = data;
    }
}

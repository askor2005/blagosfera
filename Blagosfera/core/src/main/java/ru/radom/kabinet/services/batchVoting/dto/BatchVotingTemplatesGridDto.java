package ru.radom.kabinet.services.batchVoting.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by vgusev on 14.10.2015.
 */
public class BatchVotingTemplatesGridDto {

    private boolean success = true;

    private int total = 0;

    private List<BatchVotingTemplateDto> items = new ArrayList<>();

    public BatchVotingTemplatesGridDto(boolean success, int total, List<BatchVotingTemplateDto> items) {
        this.success = success;
        this.total = total;
        this.items = items;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<BatchVotingTemplateDto> getItems() {
        return items;
    }

    public void setItems(List<BatchVotingTemplateDto> items) {
        this.items = items;
    }

    public static BatchVotingTemplatesGridDto successDtoFromDomain(int count, List<BatchVotingTemplateDto> entityItems) {
        return new BatchVotingTemplatesGridDto(true, count, entityItems);
    }

    public static BatchVotingTemplatesGridDto failDto() {
        return new BatchVotingTemplatesGridDto(false, 0, null);
    }
}

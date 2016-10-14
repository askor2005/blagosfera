package ru.radom.kabinet.web.voting.dto;

import lombok.Data;

import java.util.List;

/**
 *
 * Created by vgusev on 11.05.2016.
 */
@Data
public class VoteItemsListDto {

    private boolean success;
    private int total;
    private List<VoteItemProtocolDto> items;

    public static VoteItemsListDto successDto(int total, List<VoteItemProtocolDto> items) {
        VoteItemsListDto result = new VoteItemsListDto();
        result.setSuccess(true);
        result.setItems(items);
        result.setTotal(total);
        return result;
    }

    public static VoteItemsListDto errorDto() {
        VoteItemsListDto result = new VoteItemsListDto();
        result.setSuccess(false);
        return result;
    }
}

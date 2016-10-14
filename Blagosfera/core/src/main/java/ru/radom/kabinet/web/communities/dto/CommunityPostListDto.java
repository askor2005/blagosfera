package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.radom.kabinet.dto.community.CommunityUserPost;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка для данных таблицы должностей объединения
 * Created by vgusev on 05.04.2016.
 */
@Data
public class CommunityPostListDto {

    private boolean isSuccess;

    private int total;

    private List<CommunityPostListItemDto> items;

    private CommunityPostListDto() {}

    public CommunityPostListDto(List<CommunityUserPost> communityUserPosts, int count) {
        if (communityUserPosts != null) {
            List<CommunityPostListItemDto> items = new ArrayList<>();
            int index = 1;
            for (CommunityUserPost communityUserPost : communityUserPosts) {
                items.add(new CommunityPostListItemDto(communityUserPost, index++));
            }
            setItems(items);
        }
        setTotal(count);
        setSuccess(true);
    }

    public static CommunityPostListDto toErrorDto() {
        CommunityPostListDto result = new CommunityPostListDto();
        result.setSuccess(false);
        return result;
    }
}

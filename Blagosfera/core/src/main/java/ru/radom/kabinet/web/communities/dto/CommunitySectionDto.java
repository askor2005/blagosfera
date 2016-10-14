package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunitySectionDomain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 12.03.2016.
 */
@Data
public class CommunitySectionDto {

    private Long id;

    private String name;

    /**
     * для отображения пользователям, пишем по-русски
     */
    private String title;

    private String link;

    private List<CommunitySectionDto> children;

    private boolean isVisible;

    public static CommunitySectionDto toDto(CommunitySectionDomain communitySection, List<Long> visibleIds) {
        CommunitySectionDto result = new CommunitySectionDto();
        result.setId(communitySection.getId());
        result.setName(communitySection.getName());
        result.setTitle(communitySection.getTitle());
        result.setLink(communitySection.getLink());
        if (visibleIds != null && visibleIds.contains(result.getId())) {
            result.setVisible(true);
        } else {
            result.setVisible(false);
        }

        if (communitySection.getChildren() != null) {
            List<CommunitySectionDto> childrenResult = new ArrayList<>();
            List<CommunitySectionDomain> children = communitySection.getChildren();
            childrenResult.addAll(children.stream().map(child -> toDto(child, visibleIds)).collect(Collectors.toList()));
            result.setChildren(childrenResult);
        }
        return result;
    }

    public static List<CommunitySectionDto> toDtoList(List<CommunitySectionDomain> communitySections, List<Long> visibleIds) {
        List<CommunitySectionDto> result = new ArrayList<>();
        if (communitySections != null) {
            for (CommunitySectionDomain communitySection : communitySections) {
                result.add(toDto(communitySection, visibleIds));
            }
        }
        return result;
    }
}

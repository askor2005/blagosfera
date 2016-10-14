package ru.radom.kabinet.web.communities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.askor.blagosfera.domain.community.Community;

import java.util.ArrayList;
import java.util.List;

/**
 * Обёртка для списка дочерних объединений на странице назначения на должности
 * Created by vgusev on 05.04.2016.
 */
@Data
@AllArgsConstructor
public class CommunityChildPostRequestDto {

    private Long id;

    private String name;

    public static List<CommunityChildPostRequestDto> toDtoList(List<Community> children) {
        List<CommunityChildPostRequestDto> result = null;
        if (children != null && !children.isEmpty()) {
            result = new ArrayList<>();
            for (Community child : children) {
                result.add(new CommunityChildPostRequestDto(child.getId(), child.getName()));
            }
        }
        return result;
    }
}

package ru.radom.kabinet.web.communities.dto;

import lombok.Data;
import ru.askor.blagosfera.domain.community.CommunityPermission;

/**
 *
 * Created by vgusev on 30.03.2016.
 */
@Data
public class CommunityPermissionDto {

    private Long id;

    private String title;

    private String description;

    public CommunityPermissionDto(CommunityPermission communityPermission){
        setId(communityPermission.getId());
        setTitle(communityPermission.getTitle());
        setDescription(communityPermission.getDescription());
    }
}

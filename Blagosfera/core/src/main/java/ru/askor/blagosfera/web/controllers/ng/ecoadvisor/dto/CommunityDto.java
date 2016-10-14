package ru.askor.blagosfera.web.controllers.ng.ecoadvisor.dto;

import ru.askor.blagosfera.domain.community.Community;

import java.util.ArrayList;
import java.util.List;

public class CommunityDto {

    public long id;
    public String name;
    public String shortName;
    public String picture;
    public List<CommunityDto> communities = new ArrayList<>();

    public CommunityDto() {
    }

    /*@Deprecated
    public CommunityDto(CommunityEntity community) {
        this(community, true);
    }*/

    public CommunityDto(Community community) {
        this(community, true);
    }

    /*@Deprecated
    public CommunityDto(CommunityEntity community, boolean withSubroups) {
        id = community.getId();
        name = community.getRusFullName();
        shortName = community.getRusShortName();
        picture = community.getAvatarUrl();

        if (withSubroups) {
            for (CommunityEntity subgroup : community.getChildren()) {
                communities.add(new CommunityDto(subgroup, withSubroups));
            }
        }
    }*/

    public CommunityDto(Community community, boolean withSubroups) {
        id = community.getId();
        name = community.getFullRuName();
        shortName = community.getShortRuName();
        picture = community.getAvatarUrl();

        if (withSubroups) {
            for (Community subgroup : community.getCommunitiesMembers()) {
                communities.add(new CommunityDto(subgroup, withSubroups));
            }
        }
    }
}

package ru.askor.blagosfera.core.services.userlist.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.userlist.UserListDataSource;
import ru.askor.blagosfera.core.services.userlist.UserListDataSourceProvider;
import ru.askor.blagosfera.core.services.userlist.UserListPageData;
import ru.askor.blagosfera.core.services.userlist.dto.UserForListDto;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunityMember;
import ru.askor.blagosfera.domain.community.CommunityMemberStatus;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.CommunityMemberDomainService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Service
@Transactional
public class CommunityUserListDataSourceProvider implements UserListDataSourceProvider {

    private static final String TYPE = "CommunityUser";

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private CommunityMemberDomainService communityMemberDomainService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<UserListDataSource> getUserListDataSources(User user) {
        List<UserListDataSource> result = new ArrayList<>();
        List<Community> communities = communityDataService.getList(
                user.getId(), false, Collections.singletonList(CommunityMemberStatus.MEMBER), null, 0, Integer.MAX_VALUE,
                null, null, null, null, null, false, false, "id", false
        );
        if (communities != null) {
            for (Community community : communities) {
                result.add(new UserListDataSource(community.getId(), getType(), community.getName()));
            }
        }
        return result;
    }

    @Override
    public UserListDataSource getUserListDataSourceById(Long sourceId) {
        Community community = communityDataService.getByIdMinData(sourceId);
        return community != null ? new UserListDataSource(community.getId(), getType(), community.getName()) : null;
    }

    @Override
    public UserListPageData getBySourceId(Long sourceId, int pageNumber, int perPageCount) {
        List<CommunityMember> communityMembers = communityMemberDomainService.getByCommunityIdAndStatus(sourceId, CommunityMemberStatus.MEMBER);
        List<User> users = null;
        if (communityMembers != null) {
            users = new ArrayList<>();
            for (CommunityMember communityMember : communityMembers) {
                users.add(communityMember.getUser());
            }
        }
        // TODO
        UserForListDto.toDtoList(users, null);
        // TODO
        return new UserListPageData(0, UserForListDto.toDtoList(users, null));
    }


}

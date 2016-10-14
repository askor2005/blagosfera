package ru.askor.blagosfera.data.jpa.repositories.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.communities.CommunityPermissionEntity;

import java.util.List;

public interface CommunityPermissionRepository extends JpaRepository<CommunityPermissionEntity, Long> {

    @Query(value = "select distinct cp.name "+
            "   from community_permissions cp "+
            "	join community_security_permissions_communities cspc on cp.id = cspc.community_permission_id "+
            "   join community_posts_permissions cpp on cpp.permission_id = cp.id "+
            "   join community_members_posts cmp on cmp.post_id = cpp.post_id "+
            "   join community_members cm on cm.id = cmp.member_id "+
            "  where cp.security_role = true and cm.sharer_id = :userId", nativeQuery = true)
    List<String> findSecurityPermissions(@Param("userId") Long userId);
}

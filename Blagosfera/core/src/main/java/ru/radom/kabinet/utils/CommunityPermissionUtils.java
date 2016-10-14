package ru.radom.kabinet.utils;

/**
 * Created by ebelyaev on 10.08.2015.
 */
public class CommunityPermissionUtils {
    public static final String TRANSFER_MONEY_PERMISSION = "TRANSFER_MONEY";

    /**
     * Проверяет имеет ли sharer, который является членом объединения community, ращрешения с именем permissionName.
     *
     * @param sharer
     * @param community
     * @param permissionName
     * @return true - имеет, false - не имеет.
     */
    /*public static boolean hasPermission(Sharer sharer, CommunityEntity community, String permissionName) {
        List<CommunityMemberEntity> communityMembers = community.getMembers();
        for (CommunityMemberEntity member: communityMembers) {
            if(member.getUser().equals(sharer)) {
                List<CommunityPostEntity> communityPosts = member.getPosts();
                for(CommunityPostEntity post: communityPosts) {
                    List<CommunityPermissionEntity> communityPermissions = post.getPermissions();
                    for(CommunityPermissionEntity permission: communityPermissions) {
                        if(permission.getName().equalsIgnoreCase(permissionName)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }*/
}

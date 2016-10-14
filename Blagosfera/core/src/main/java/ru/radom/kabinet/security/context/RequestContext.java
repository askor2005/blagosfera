package ru.radom.kabinet.security.context;

import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.communities.CommunityEntity;
import ru.radom.kabinet.model.communities.CommunityMemberEntity;

/**
 * сервис облегчающий работу с данными в контексте текущего запроса
 */
public interface RequestContext {

    /**
     * Позволяет определить, авторизовался ли пользователь во время текущего запроса
     * @return true - только что авторизовался, false - авторизовался ранее
     */
    boolean isJustLogged();

    /**
     * Позволяет установить флаг isJustLogged
     * @param isJustLogged
     */
    void setIsJustLogged(boolean isJustLogged);

    CommunityEntity getCommunity();

    void setCommunity(CommunityEntity community);

    Long getCommunityId();

    void setCommunityId(Long id);

    CommunityMemberEntity getCommunityMember();

    void setCommunityMember(CommunityMemberEntity communityMember);

    boolean isCommunityMember();

    boolean isActiveCommunityMember();

    boolean isCommunityCreator();

    boolean isRootCommunityCreator();

    /**
     * Позволяет установить данные пользователя (не авторизованный пользователь)
     * @param tempUserEntity
     */
    void setTempUserEntity(UserEntity tempUserEntity);

    /**
     * Позволяет получить пользователя (не авторизованный пользователь)
     * @return
     */
    UserEntity getTempUserEntity();
}

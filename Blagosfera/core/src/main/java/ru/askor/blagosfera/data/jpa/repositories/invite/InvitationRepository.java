package ru.askor.blagosfera.data.jpa.repositories.invite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.radom.kabinet.model.invite.InvitationEntity;

import java.util.List;

/**
 * Хранилище приглашений всистему
 */
public interface InvitationRepository extends JpaRepository<InvitationEntity, Long>,JpaSpecificationExecutor<InvitationEntity> {

    /**
     * Позволяет найти в базе последнее приглашение по адресу почты приглашенного
     * @param email адрес почты приглашенного
     * @return InvitationEntity приглашение или null, если оно не было найдено в базе
     */
    InvitationEntity findFirstByEmailOrderByIdDesc(String email);

    /**
     * Позволяет найти в базе последнее приглашение по hash url
     * @param hashUrl
     * @return InvitationEntity приглашение или null, если оно не было найдено в базе
     */
    InvitationEntity findFirstByHashUrlOrderByIdDesc(String hashUrl);

    List<InvitationEntity> findByUser_IdAndHashUrlIsNotNullAndHashUrlNot(Long userId, String hashUrl);

    /**
     * возвращает приглашения по статусу для данного пользователя
     * @param status
     * @param userId
     * @return
     */
    List<InvitationEntity> findAllByStatusAndInvitedUser_Id(Integer status, Long userId);
}

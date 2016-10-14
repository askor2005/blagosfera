package ru.askor.blagosfera.data.jpa.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.model.chat.DialogEntity;

import java.util.List;
import java.util.Set;

public interface DialogsRepository extends JpaRepository<DialogEntity, Long> {

    //@Query("SELECT d FROM DialogEntity d INNER JOIN d.sharers s WHERE d.name = :name and s IN (:sharers)")
    DialogEntity getByNameAndUsers(String name, List<UserEntity> users);

    List<DialogEntity> findByIdInAndUsers_Id(Set<Long> ids, Long userId);

    DialogEntity getByIdAndUsers_Id(Long dialogId, Long userId);


    @Query(
            "select d " +
                    " from DialogEntity d where size(d.users) = 2 and " +
                    "exists(select 1 from DialogEntity d2 join d2.users sh where d2.id = d.id and sh.id = :userId) and " +
                    "exists(select 1 from DialogEntity d3 join d3.users sh2 where d3.id = d.id and sh2.id = :otherId) and " +
                    "exists(select 1 from ContactEntity cnt where cnt.user.id = :userId and cnt.other.id = :otherId) and " +
                    "exists(select 1 from ChatMessageReceiver cmr2 where cmr2.chatMessage.dialog.id = d.id and cmr2.receiver.id = :userId and cmr2.read = false)"
    )
    List<DialogEntity> getDialogsByUsersWithUnreadMessages(@Param("userId") Long userId, @Param("otherId") Long otherId, Pageable pageable);

    @Query("select d "+
            " from DialogEntity d where size(d.users) = 2 and " +
            "exists(select 1 from DialogEntity d2 join d2.users sh where d2.id = d.id and sh.id = :userId) and " +
            "exists(select 1 from DialogEntity d3 join d3.users sh2 where d3.id = d.id and sh2.id in (:otherIds)) and " +
            "(exists(select 1 from ContactEntity cnt where cnt.user.id = :userId and cnt.other.id in (:otherIds)) or " +
            "(true = :withUnreadMessages and exists(select 1 from ChatMessageReceiver cmr2 where cmr2.chatMessage.dialog.id = d.id and cmr2.receiver.id = :userId and cmr2.read = false)) )"
    )
    List<DialogEntity> getDialogsBySharersOrUnreadMessages(@Param("userId") Long userId, @Param("otherIds") List<Long> otherIds, @Param("withUnreadMessages") boolean withUnreadMessages);


    @Query("select d "+
            " from DialogEntity d where size(d.users) = 2 and " +
            "exists(select 1 from DialogEntity d2 join d2.users sh where d2.id = d.id and sh.id = :userId) and " +
            "exists(select 1 from DialogEntity d3 join d3.users sh2 where d3.id = d.id and sh2.id <> :userId and LOWER(sh2.searchString) like :searchString)"
            //"exists(select 1 from DialogEntity d3 join d3.users sh2 where d3.id = d.id and sh2.id <> :userId and LOWER(sh2.searchString) like :searchString and (sh2.online = true or false = :online and sh2.online = false))"
    )
    List<DialogEntity> searchContacts(@Param("userId") Long userId, @Param("searchString") String searchString, Pageable pageable);
}

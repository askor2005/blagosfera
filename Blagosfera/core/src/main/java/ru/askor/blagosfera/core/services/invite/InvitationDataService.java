package ru.askor.blagosfera.core.services.invite;

import ru.askor.blagosfera.domain.invite.Invitation;
import ru.radom.kabinet.dto.InvitesTableDataDto;
import ru.radom.kabinet.model.invite.InvitationEntity;
import ru.radom.kabinet.model.invite.InviteFilter;
import ru.radom.kabinet.web.invite.dto.InviteCountDto;

/**
 * Created by vtarasenko on 15.04.2016.
 */
public interface InvitationDataService {

    Invitation findFirstByEmail(String email);

    Invitation getByHashUrl(String hashUrl);

    boolean existsInvites(String email);

    Invitation save(Invitation invite);

    Invitation getById(Long id);

    InvitesTableDataDto getListByFilter(Long userId, InviteFilter filter);

    InviteCountDto getInviteCountData(Long userId);

    InvitationEntity findAcceptedInvitationByUserId(Long userId);
}

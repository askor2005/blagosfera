package ru.radom.kabinet.services;

import ru.askor.blagosfera.domain.invite.Invitation;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.model.UserEntity;
import ru.radom.kabinet.web.invite.dto.AcceptInvitationResult;
import ru.radom.kabinet.web.invite.dto.RejectInvitationResult;

import java.util.List;
import java.util.Map;

/**
 * Created by vtarasenko on 17.04.2016.
 */
public interface InvitationService {
    Invitation createInvite(String email, String invitedLastName, String invitedFirstName, String invitedFatherName,
                            String invitedGender, Boolean guarantee, Integer howLongFamiliar, List<Long> relationships,
                            Long userId);

    void sendToEmail(Invitation invite, Long userId);

    AcceptInvitationResult acceptInvitation(String hash, String password, String base64AvatarSrc, String base64Avatar, boolean needSendPassword);

    RejectInvitationResult rejectInvitation(String hash);

    Map<String, String> fillInvitationMap(String hash);

    void changeAuthDataOfInvited(User user, String password, Invitation invite);

    Invitation getById(Long id);

    boolean existsInvites(String email);

    Invitation getByHashUrl(String hash);

    Invitation findAcceptedInvitationByUserId(Long userId);
}

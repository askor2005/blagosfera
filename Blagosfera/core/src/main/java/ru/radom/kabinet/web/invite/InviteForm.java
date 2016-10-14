package ru.radom.kabinet.web.invite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.askor.blagosfera.domain.invite.InviteRelationshipTypeDomain;
import ru.radom.kabinet.model.invite.InviteRelationshipType;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InviteForm {
    private String email;

    //фамилия приглашенного
    private String invitedLastName;

    //имя приглашенного
    private String invitedFirstName;

    //отчество приглашенного
    private String invitedFatherName;

    //пол приглашенного (М / Ж)
    private String invitedGender;

    //признак ручаюсь за него или не ручаюсь
    private boolean guarantee;

    //сколько лет знаком
    private Integer howLongFamiliar;

    //отношения (кто он для вас), айдишники
    private List<Long> relationships;

}
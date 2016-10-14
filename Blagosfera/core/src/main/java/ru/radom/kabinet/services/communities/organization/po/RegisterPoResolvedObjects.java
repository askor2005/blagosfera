package ru.radom.kabinet.services.communities.organization.po;

import lombok.Data;
import ru.askor.blagosfera.domain.community.OkvedDomain;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.askor.blagosfera.domain.user.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * Created by vgusev on 01.02.2016.
 */
@Data
public class RegisterPoResolvedObjects {

    private OkvedDomain mainOkved;

    private List<OkvedDomain> additionalOkveds;

    private Set<User> founders;

    private Set<User> participantsInSoviet;

    private Set<User> participantsInAuditCommittee;

    private ListEditorItem officeOwnerShipListEditorItem;

    private ListEditorItem associationFormListItem;

    private Integer sovietOfficePeriod;

    private Integer participantsOfBoardOfficePeriod;

    private Integer countDaysPerMeetingOfBoard;

    private Integer quorumMeetingOfBoard;

    private Integer participantsAuditCommitteeOfficePeriod;

    private boolean hasStamp;

    private Integer countDaysToQuiteFromPo;

    private Integer countMonthToSharerPay;

    private Long minCreditApproveSovietPO;

    private Long minContractSumApproveSovietPO;


    public RegisterPoResolvedObjects() {
        this.additionalOkveds = new ArrayList<>();
        this.founders = new HashSet<>();
        this.participantsInSoviet = new HashSet<>();
        this.participantsInAuditCommittee = new HashSet<>();
    }
}

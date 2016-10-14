package ru.radom.kabinet.services.communities.organizationmember.behavior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.askor.blagosfera.domain.community.Community;
import ru.radom.kabinet.dao.rameralisteditor.RameraListEditorItemDAO;
import ru.radom.kabinet.model.rameralisteditor.RameraListEditorItem;
import ru.radom.kabinet.services.communities.organizationmember.behavior.kuch.KuchOrganizationMemberBehavior;
import ru.radom.kabinet.services.communities.organizationmember.behavior.po.POOrganizationMemberBehavior;

/**
 *
 * Created by vgusev on 20.10.2015.
 */
@Component
public class OrganizationMemberBehaviorResolver {

    private enum CommunityType {
        PO, KUCH, DEFAULT;
    }

    @Autowired
    private DefaultOrganizationMemberBehavior defaultOrganizationMemberBehavior;

    @Autowired
    private POOrganizationMemberBehavior poOrganizationMemberBehavior;

    @Autowired
    private KuchOrganizationMemberBehavior kuchOrganizationMemberBehavior;

    @Autowired
    private RameraListEditorItemDAO rameraListEditorItemDAO;

    private CommunityType getCommunityType(Community community) {
        CommunityType result = CommunityType.DEFAULT;
        if (community.getAssociationForm() != null) {
            RameraListEditorItem rameraListEditorItem = rameraListEditorItemDAO.getById(community.getAssociationForm().getId());
            if (rameraListEditorItem == null) {
                // do nothing
            } else if (Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE.equals(rameraListEditorItem.getMnemoCode())) {
                result = CommunityType.PO;
            } else if (Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE.equals(rameraListEditorItem.getMnemoCode())) {
                result = CommunityType.KUCH;
            }
        }
        return result;
    }

    public IOrganizationMemberBehavior getOrganizationMemberBehavior(Community community){
        CommunityType communityType = getCommunityType(community);
        IOrganizationMemberBehavior result = defaultOrganizationMemberBehavior;
        switch (communityType) {
            case PO:
                result = poOrganizationMemberBehavior;
                break;
            case KUCH:
                result = kuchOrganizationMemberBehavior;
                break;
            default:
                break;
        }
        return result;
    }

}

package ru.radom.kabinet.services.communities.sharermember.behavior;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.listEditor.ListEditorItem;
import ru.radom.kabinet.module.rameralisteditor.service.ListEditorItemDomainService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.communities.sharermember.behavior.kuch.KuchSharerCommunityMemberBehavior;
import ru.radom.kabinet.services.communities.sharermember.behavior.po.POSharerCommunityMemberBehavior;
import ru.radom.kabinet.utils.SpringUtils;

/**
 *
 * Created by vgusev on 28.10.2015.
 */
@Service
public class SharerCommunityMemberBehaviorResolver {

    public enum CommunityType {
        PO, KUCH, DEFAULT;
    }

    @Autowired
    private DefaultSharerCommunityMemberBehavior defaultSharerCommunityMemberBehavior;

    @Autowired
    private POSharerCommunityMemberBehavior poSharerCommunityMemberBehavior;

    @Autowired
    private KuchSharerCommunityMemberBehavior kuchSharerCommunityMemberBehavior;

    @Autowired
    private ListEditorItemDomainService listEditorItemDomainService;

    @Autowired
    private CommunityDataService communityDomainService;

    public CommunityType getCommunityType(Community community) {
        CommunityType result = CommunityType.DEFAULT;
        if (community.getAssociationForm() == null) {
            community = communityDomainService.getByIdMediumData(community.getId());
        }

        if (community.getAssociationForm() != null) {
            ListEditorItem listEditorItem = listEditorItemDomainService.getById(community.getAssociationForm().getId());
            if (listEditorItem == null) {
                // do nothing
            } else if (Community.COOPERATIVE_SOCIETY_LIST_ITEM_CODE.equals(listEditorItem.getCode())) {
                result = CommunityType.PO;
            } else if (Community.COOPERATIVE_PLOT_ASSOCIATION_FORM_CODE.equals(listEditorItem.getCode())) {
                result = CommunityType.KUCH;
            }
        }
        return result;
    }

    public ISharerCommunityMemberBehavior getSourceBehavior(Community community){
        CommunityType communityType = getCommunityType(community);
        ISharerCommunityMemberBehavior result = defaultSharerCommunityMemberBehavior;
        switch (communityType) {
            case PO:
                result = poSharerCommunityMemberBehavior;
                break;
            case KUCH:
                result = kuchSharerCommunityMemberBehavior;
                break;
            default:
                break;
        }
        return result;
    }

    public ISharerCommunityMemberBehavior getSourceBehavior(Long communityId){
        Community community = communityDomainService.getByIdFullData(communityId);
        return getSourceBehavior(community);
    }

    public ISharerCommunityMemberBehavior getBehavior(Long communityId){
        Community community = communityDomainService.getByIdFullData(communityId);
        ISharerCommunityMemberBehavior result = getSourceBehavior(community);
        if (community.isNeedCreateDocuments()) {
            result = SpringUtils.getBean("documentsSharerCommunityMemberBehavior", result);
        }
        result = SpringUtils.getBean("publishEventsCommunityMemberBehavior", result);

        return result;
    }
}

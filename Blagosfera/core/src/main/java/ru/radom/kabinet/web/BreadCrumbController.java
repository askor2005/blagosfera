package ru.radom.kabinet.web;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.askor.blagosfera.domain.community.Community;
import ru.askor.blagosfera.domain.community.CommunitySectionDomain;
import ru.askor.blagosfera.domain.section.SectionDomain;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.business.services.VotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.askor.voting.domain.Voting;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.services.communities.CommunitiesService;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.section.SectionService;
import ru.radom.kabinet.utils.CommonConstants;
import ru.radom.kabinet.utils.StringUtils;
import ru.radom.kabinet.utils.VarUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;
import ru.radom.kabinet.web.utils.Breadcrumb;
import ru.radom.kabinet.web.utils.BreadcrumbItem;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by vgusev on 15.03.2016.
 */
@Controller
public class BreadCrumbController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private CommunitiesService communitiesService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private VotingService votingService;

    @RequestMapping(value = "/breadcrumb.json", method = {RequestMethod.GET, RequestMethod.POST}, produces = CommonConstants.RESPONSE_JSON_MEDIA_TYPE)
    @ResponseBody
    public Breadcrumb getBreadCrumb(@RequestParam(value = "link", required = true) String link) {
        Long userId = SecurityUtils.getUser().getId();
        Breadcrumb result = null;
        if (link != null) {
            try {
                link = URLDecoder.decode(link, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (link != null && link.startsWith("/group/")) { // Создать путь для объединения
            String[] linkParts = link.split("/");
            if (linkParts.length > 1) {
                String seoLink = linkParts[2];
                String[] communityLinkParts = link.split(seoLink);
                String communitySectionLink = "";
                if (communityLinkParts.length == 2) {
                    communitySectionLink = communityLinkParts[1].equals("/") ? "" : communityLinkParts[1];
                }
                Long communityId = communityDataService.findCommunityId(seoLink);
                Community community = communityDataService.getByIdMediumData(communityId);
                boolean isMember = communityDataService.isSharerMember(communityId, userId);
                boolean isCreator = userId.equals(community.getCreator().getId());

                SectionDomain sectionDomain = sectionService.getByName("ramera");

                result = new Breadcrumb().add(sectionDomain.getTitle(), sectionDomain.getLink())
                        .addItem(communitiesService.getBreadcrumbCommonItems(isMember, isCreator, community))
                        .add(community.getFullRuName(), community.getLink());

                if (link.endsWith("account")) {
                    result.add(new BreadcrumbItem("Баланс", community.getLink() + "/account"));
                }

                if (!communitySectionLink.equals("")) {
                    CommunitySectionDomain communitySection = communityDataService.getCommunitySectionByLink(communitySectionLink);
                    if (communitySection != null) {
                        if (communitySection.getParent() != null) {
                            result.add(communitySection.getParent().getTitle(), "#");
                        }
                        result.add(communitySection.getTitle(), community.getLink() + communitySection.getLink());
                    }
                }
            }
        } else if (link != null && link.startsWith("/votingsystem/")) {
            result = getVotingBreadCrumb(link, userId);
        } else {
            SectionDomain currentSection = sectionService.findSectionByLink(link, userId);
            result = getBreadcrumbBySection(currentSection, userId);
        }
        return result;
    }

    private Breadcrumb getBreadcrumbBySection(SectionDomain section, Long userId) {
        Breadcrumb result = new Breadcrumb();
        List<SectionDomain> sectionsHierarchy = sectionService.getHierarchy(section.getId(), userId);
        if (sectionsHierarchy != null) {
            for (SectionDomain sectionHierarchy : sectionsHierarchy) {
                if (StringUtils.hasLength(sectionHierarchy.getTitle())) {
                    result.add(sectionHierarchy.getTitle(), StringUtils.hasLength(sectionHierarchy.getLink()) ? sectionHierarchy.getLink() : "#");
                }
            }
        }
        return result;
    }

    private Breadcrumb getVotingBreadCrumb(String link, Long userId) {
        Breadcrumb result = null;
        Map<String, String> parametersMap = parseLinkToParameters(link);

        if (parametersMap != null) {
            SectionDomain currentSection = sectionService.getByName("userbatchvotings");

            BatchVoting batchVoting = null;
            Voting voting = null;
            if (link.contains("registrationInVoting")) { // Страница регистрации в собрании
                Long batchVotingId = VarUtils.getLong(parametersMap.get("batchVotingId"), null);
                if (batchVotingId != null) {
                    try {
                        batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (link.contains("votingPage.html")) {
                Long votingId = VarUtils.getLong(parametersMap.get("votingId"), null);
                if (votingId != null) {
                    try {
                        voting = votingService.getVoting(votingId, false, false);
                        batchVoting = batchVotingService.getBatchVotingByVotingId(votingId, false, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (batchVoting != null) {
                result = getBreadCrumbByBatchVoting(batchVoting, currentSection, userId);
            }
            if (result != null && voting != null) {
                result.add(voting.getSubject(), "/votingsystem/votingPage.html?votingId=" + voting.getId());
            }
        }
        return result;
    }

    private Breadcrumb getBreadCrumbByBatchVoting(BatchVoting batchVoting, SectionDomain currentSection, Long userId) {
        Breadcrumb result;
        String communityIdStr = batchVoting.getAdditionalData().get(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME);
        Long communityId = VarUtils.getLong(communityIdStr, null);
        if (communityId != null) {
            Community community = communityDataService.getByIdMediumData(communityId);
            boolean isMember = communityDataService.isSharerMember(communityId, userId);
            boolean isCreator = userId.equals(community.getCreator().getId());

            SectionDomain sectionDomain = sectionService.getByName("ramera");

            result = new Breadcrumb().add(sectionDomain.getTitle(), sectionDomain.getLink())
                    .addItem(communitiesService.getBreadcrumbCommonItems(isMember, isCreator, community))
                    .add(community.getFullRuName(), community.getLink());
            result.add("Волеизъявления", "#").add("Собрания с моим участием", community.getLink() + "/batchvotings");
        } else {
            result = getBreadcrumbBySection(currentSection, userId);
        }
        result.add(batchVoting.getSubject(), "/votingsystem/batchVotingPage?batchVotingId=" + batchVoting.getId());
        return result;
    }

    private Map<String, String> parseLinkToParameters(String link) {
        Map<String, String> result = null;
        List<NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(link), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (params != null) {
            result = new HashMap<>();
            for (NameValuePair param : params) {
                result.put(param.getName(), param.getValue());
            }
        }
        return result;
    }
}

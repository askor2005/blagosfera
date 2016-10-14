package ru.radom.kabinet.services.sharer.impl;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate.BatchVotingTemplateRepository;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.model.votingtemplate.BatchVotingTemplateEntity;
import ru.radom.kabinet.services.batchVoting.CommunityBatchVotingTemplateService;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingVotersPageResultDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserBatchVotingService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.*;

/**
 *
 * Created by vgusev on 23.05.2016.
 */
@Service
@Transactional
public class UserBatchVotingServiceImpl implements UserBatchVotingService {

    private static int COUNT_ELEMENTS_IN_GRID_PAGE = 25;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private BatchVotingTemplateRepository batchVotingTemplateRepository;

    @Autowired
    private CommunityDataService communityDataService;

    @Autowired
    private CommunityBatchVotingTemplateService communityBatchVotingTemplateService;

    @Override
    public BatchVotingsPageResultDto filterBatchVotings(
            Long ownerId, Long voterId,
            Date startDateStart, Date startDateEnd,
            Date endDateStart, Date endDateEnd,
            Map<String, String> parameters,
            BatchVotingState batchVotingState, String subject, int page,
            Long currentUserId) {

        LocalDateTime startDateStartLDT = null;
        LocalDateTime startDateEndLDT = null;
        LocalDateTime endDateStartLDT = null;
        LocalDateTime endDateEndLDT = null;

        if (startDateStart != null) {
            startDateStart = DateUtils.getDayBegin(startDateStart);
            startDateStartLDT = ru.askor.blagosfera.core.util.DateUtils.toLocalDateTime(startDateStart);
        }
        if (startDateEnd != null) {
            startDateEnd = DateUtils.getDayEnd(startDateEnd);
            startDateEndLDT = ru.askor.blagosfera.core.util.DateUtils.toLocalDateTime(startDateEnd);
        }
        if (endDateStart != null) {
            endDateStart = DateUtils.getDayBegin(endDateStart);
            endDateStartLDT = ru.askor.blagosfera.core.util.DateUtils.toLocalDateTime(endDateStart);
        }
        if (endDateEnd != null) {
            endDateEnd = DateUtils.getDayEnd(endDateEnd);
            endDateEndLDT = ru.askor.blagosfera.core.util.DateUtils.toLocalDateTime(endDateEnd);
        }
        List<BatchVoting> batchVotings = null;
        long count = 0l;
        Map<Long, String> templateLinks = null;

        try {
            BatchVotingPage batchVotingsPage = batchVotingService.getBatchVotingPage(
                    ownerId,
                    voterId,
                    startDateStartLDT, startDateEndLDT,
                    endDateStartLDT, endDateEndLDT,
                    parameters,
                    batchVotingState,
                    subject,
                    page, COUNT_ELEMENTS_IN_GRID_PAGE);
            count = batchVotingsPage.getTotalElements();
            batchVotings = batchVotingsPage.getContent();

            templateLinks = new HashMap<>();
            for (BatchVoting batchVoting : batchVotings) {
                if (batchVoting.getOwnerId().equals(currentUserId)) {
                    BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateRepository.findByBatchVoting(batchVoting.getId());
                    if (batchVotingTemplate != null && batchVotingTemplate.getCommunity() != null) {
                        String link = "/group/" + batchVotingTemplate.getCommunity().getId() + "/batchVotingConstructor.html?templateId=" + batchVotingTemplate.getId();
                        templateLinks.put(batchVoting.getId(), link);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtils.check(true, e.getMessage());
        }
        return new BatchVotingsPageResultDto(count, batchVotings, templateLinks);
    }

    @Override
    public BatchVotingVotersPageResultDto filterBatchVotingVoters(Long batchVotingId, RegisteredVoterStatus registeredVoterStatus, String voterName, int page) throws VotingSystemException {
        return communityBatchVotingTemplateService.filterBatchVotingVoters(batchVotingId, registeredVoterStatus, voterName, page);
    }
}

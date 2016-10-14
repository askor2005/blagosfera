package ru.radom.kabinet.services.batchVoting;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.data.jpa.repositories.UserRepository;
import ru.askor.blagosfera.data.jpa.repositories.batchvotingtemplate.BatchVotingTemplateRepository;
import ru.askor.blagosfera.data.jpa.repositories.community.CommunityRepository;
import ru.askor.blagosfera.domain.user.User;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.*;
import ru.askor.voting.domain.exception.VotingSystemException;
import ru.radom.kabinet.model.votingtemplate.BatchVotingTemplateEntity;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingTemplateDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingVotersPageResultDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsByTemplateGridDto;
import ru.radom.kabinet.services.batchVoting.dto.BatchVotingsPageResultDto;
import ru.radom.kabinet.services.communities.CommunityDataService;
import ru.radom.kabinet.services.sharer.UserDataService;
import ru.radom.kabinet.utils.DateUtils;
import ru.radom.kabinet.utils.exception.ExceptionUtils;
import ru.radom.kabinet.voting.BatchVotingConstants;

import java.util.*;

/**
 * Сервис для создания собраний для объединения
 * Created by vgusev on 11.10.2015.
 */
@Service
@Transactional
public class CommunityBatchVotingTemplateService extends BatchVotingTemplateService {

    @Autowired
    private BatchVotingTemplateRepository batchVotingTemplateRepository;

    @Autowired
    private RequestContext radomRequestContext;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private CommunityDataService communityDataService;

    private static final int COUNT_ELEMENTS_IN_GRID_PAGE = 25;

    private void checkDto(Long communityId){
        if (communityId == null || communityId < 1) {
            throw new RuntimeException("Не выбрано объединение в котором будет проводится собрание.");
        }
    }

    public BatchVotingTemplateEntity getBatchVotingTemplate(BatchVotingTemplateDto batchVotingDto, Long communityId) {
        checkDto(communityId);

        if (batchVotingDto.getVotersAllowed().size() < 3) {
            throw new RuntimeException("В собрании должно участвовать минимум 3 человека");
        }

        BatchVotingTemplateEntity batchVotingTemplate = super.getBatchVotingTemplate(batchVotingDto);
        batchVotingTemplate.setCommunity(communityRepository.findOne(communityId));
        batchVotingTemplate.setCreator(userRepository.findOne(SecurityUtils.getUser().getId()));
        return batchVotingTemplate;
    }

    public void save(BatchVotingTemplateEntity batchVotingTemplate, Long communityId, Long userId) {
        batchVotingTemplate.setCommunity(communityRepository.findOne(communityId));
        batchVotingTemplate.setCreator(userRepository.findOne(userId));
        super.save(batchVotingTemplate);
    }

    /**
     * Поиск с пейджингом
     * @param subject
     * @param page
     * @return
     */
    public List<BatchVotingTemplateEntity> findBySubject(String subject, int page, Long communityId) {
        // page c 0
        Pageable pageable = new PageRequest(page, COUNT_ELEMENTS_IN_GRID_PAGE);
        return batchVotingTemplateRepository.findByCommunity_IdAndSubjectLikeIgnoreCaseOrderByIdDesc(communityId, "%" + subject + "%", pageable);
    }

    /**
     *
     * @param subject
     * @return
     */
    public int getCountBySubject(String subject, Long communityId) {
        return batchVotingTemplateRepository.countByCommunityIdAndSubjectLikeIgnoreCase(communityId, "%" + subject + "%");
    }

    public BatchVotingsByTemplateGridDto getBatchVotingsByTemplate(Long templateId, int page) {
        List<BatchVoting> batchVotings = null;
        BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateRepository.getOne(templateId);
        ExceptionUtils.check(batchVotingTemplate == null, "Шаблон не найден");

        List<Long> batchVotingIds = null;
        List<Long> pageBatchVotingIds = null;
        int count = 0;
        if (batchVotingTemplate.getBatchVotings() != null) {
            batchVotingIds = new ArrayList<>(batchVotingTemplate.getBatchVotings());
            count = batchVotingIds.size();
        }

        if (batchVotingIds != null && !batchVotingIds.isEmpty()) {
            int startIndex = (page - 1) * COUNT_ELEMENTS_IN_GRID_PAGE;
            int endIndex = startIndex + COUNT_ELEMENTS_IN_GRID_PAGE;
            if (batchVotingIds.size() > startIndex) {
                endIndex = batchVotingIds.size() >= endIndex ? endIndex : batchVotingIds.size();
                pageBatchVotingIds = batchVotingIds.subList(startIndex, endIndex);
            }
        }
        if (pageBatchVotingIds != null) {
            batchVotings = new ArrayList<>();
            for (Long batchVotingId : pageBatchVotingIds) {
                BatchVoting batchVoting = null;
                try {
                    batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);
                } catch (Exception e) {
                    ExceptionUtils.check(true, e.getMessage());
                }
                batchVotings.add(batchVoting);
            }
        }
        //batchVotingService.fil
        return BatchVotingsByTemplateGridDto.successDtoFromDomain(count, batchVotings);
    }

    public BatchVotingsPageResultDto filterBatchVotings(
            Long ownerId,
            Long voterId, Date startDateStart, Date startDateEnd, Date endDateStart, Date endDateEnd,
            Map<String, String> parameters, BatchVotingState batchVotingState, String subject, int pageNumber, Long communityId, Long currentUserId) {
        // TODO Проверить роль пользователя в объединении
        //user.isAd
        //ExceptionUtils.check(user == null || user.getId() == null, "Не переданн участник системы");
        ExceptionUtils.check(communityId == null, "Не передано объединение");
        if (parameters == null) {
            parameters = new HashMap<>();
        }
        parameters.put(BatchVotingConstants.COMMUNITY_ID_ATTR_NAME, String.valueOf(communityId));

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
                    pageNumber, COUNT_ELEMENTS_IN_GRID_PAGE);
            batchVotings = batchVotingsPage.getContent();
            count = batchVotingsPage.getTotalElements();

            templateLinks = new HashMap<>();
            for (BatchVoting batchVoting : batchVotings) {
                if (batchVoting.getOwnerId().equals(currentUserId)) {
                    BatchVotingTemplateEntity batchVotingTemplate = batchVotingTemplateRepository.findByBatchVoting(batchVoting.getId());
                    if (batchVotingTemplate != null && batchVotingTemplate.getCommunity() != null) {
                        templateLinks.put(batchVoting.getId(), "/group/" + batchVotingTemplate.getCommunity().getId() +
                                "/batchVotingConstructor.html?templateId=" + batchVotingTemplate.getId());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ExceptionUtils.check(true, e.getMessage());
        }

        return new BatchVotingsPageResultDto(count, batchVotings, templateLinks);
    }

    public BatchVotingVotersPageResultDto filterBatchVotingVoters(Long batchVotingId, RegisteredVoterStatus registeredVoterStatus, String voterName, int page) throws VotingSystemException {
        BatchVoting batchVoting = batchVotingService.getBatchVoting(batchVotingId, false, false);

        List<RegisteredVoter> registeredVoters = new ArrayList<>();
        for (RegisteredVoter registeredVoter : batchVoting.getVotersAllowed()) {
            if ((registeredVoterStatus != null && registeredVoterStatus.equals(registeredVoter.getStatus())) || registeredVoterStatus == null) {
                registeredVoters.add(registeredVoter);
            }
        }

        int count = registeredVoters.size();
        //List<RegisteredVoter> registeredVoters = new ArrayList<>(batchVoting.getVotersAllowed());
        List<User> users = null;
        List<Long> userIds = new ArrayList<>();
        Map<Long, RegisteredVoterStatus> userStates = new HashMap<>();



        int startIndex = page * COUNT_ELEMENTS_IN_GRID_PAGE;
        int endIndex = startIndex + COUNT_ELEMENTS_IN_GRID_PAGE;
        List<RegisteredVoter> pageRegisteredVoters = null;
        if (registeredVoters.size() > startIndex) {
            endIndex = registeredVoters.size() >= endIndex ? endIndex : registeredVoters.size();
            pageRegisteredVoters = registeredVoters.subList(startIndex, endIndex);
        }
        if (pageRegisteredVoters != null) {
            for (RegisteredVoter registeredVoter : pageRegisteredVoters) {
                userIds.add(registeredVoter.getVoterId());
                userStates.put(registeredVoter.getVoterId(), registeredVoter.getStatus());
            }
        }
        if (!userIds.isEmpty()) {
            users = userDataService.getByIds(userIds);
        }

        return new BatchVotingVotersPageResultDto(count, users, userStates);
    }

}
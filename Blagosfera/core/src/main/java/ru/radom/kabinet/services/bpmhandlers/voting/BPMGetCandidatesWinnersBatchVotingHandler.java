package ru.radom.kabinet.services.bpmhandlers.voting;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.voting.domain.VotingItem;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.voting.dto.BPMGetCandidatesWinnersIdsDto;
import ru.radom.kabinet.voting.CommonVotingService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("getCandidatesWinnersBatchVotingHandler")
@Transactional
public class BPMGetCandidatesWinnersBatchVotingHandler implements BPMHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMCreateBatchVotingHandler.class);

    @Autowired
    private BPMBatchVotingService bpmBatchVotingService;

    @Autowired
    private SerializeService serializeService;

    @Autowired
    private CommonVotingService commonVotingService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        LOGGER.info("Задача " + taskId + ". Получение ИД победиелей в выборах.");
        BPMGetCandidatesWinnersIdsDto bpmGetCandidatesWinnersIdsDto = serializeService.toObject(parameters, BPMGetCandidatesWinnersIdsDto.class);
        List<VotingItem> votingItems = commonVotingService.getWinners(bpmGetCandidatesWinnersIdsDto.votingId);
        List<String> result = votingItems.stream().map(VotingItem::getValue).collect(Collectors.toList());

        return StringUtils.join(result, bpmGetCandidatesWinnersIdsDto.joinString);
    }
}
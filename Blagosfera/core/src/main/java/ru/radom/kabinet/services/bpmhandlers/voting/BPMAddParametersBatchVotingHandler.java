package ru.radom.kabinet.services.bpmhandlers.voting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.voting.business.services.BatchVotingService;
import ru.askor.voting.domain.BatchVoting;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.voting.dto.BPMBatchVotingAddParametersDto;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("addParametersBatchVotingHandler")
@Transactional
public class BPMAddParametersBatchVotingHandler implements BPMHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMAddParametersBatchVotingHandler.class);

    @Autowired
    private BPMBatchVotingService bpmBatchVotingService;

    @Autowired
    private BatchVotingService batchVotingService;

    @Autowired
    private SerializeService serializeService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        LOGGER.info("Задача " + taskId + ". Добавление к собранию дополнительных параметров.");
        BatchVoting batchVoting = null;
        try {
            BPMBatchVotingAddParametersDto bpmBatchVotingDto = serializeService.toObject(parameters, BPMBatchVotingAddParametersDto.class);
            batchVoting = batchVotingService.getBatchVoting(bpmBatchVotingDto.getBatchVotingId(), true, true);
            batchVoting.getAdditionalData().putAll(bpmBatchVotingDto.getAdditionalParameters());
            batchVotingService.saveAdditionalData(batchVoting);
        } catch (Exception e) {
            LOGGER.info("Задача " + taskId + ". При добавлении параметров к собранию произошли ошибки.");
            ExceptionUtils.check(true, e.getMessage());
        }
        return serializeService.toPrimitiveObject(bpmBatchVotingService.getBpmBatchVotingExtended(batchVoting));
    }
}
package ru.radom.kabinet.services.bpmhandlers.voting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.rabbit.BPMHandler;
import ru.radom.kabinet.services.SerializeService;
import ru.radom.kabinet.services.bpmhandlers.voting.dto.BPMBatchVotingDto;

import java.util.Map;

/**
 *
 * Created by vgusev on 15.08.2016.
 */
@Service("createBatchVotingHandler")
@Transactional
public class BPMCreateBatchVotingHandler implements BPMHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMCreateBatchVotingHandler.class);

    @Autowired
    private BPMBatchVotingService bpmBatchVotingService;

    @Autowired
    private SerializeService serializeService;

    @Override
    public Object handle(Map<String, Object> parameters, String taskId) {
        LOGGER.info("Запуск создания собрания.");
        BPMBatchVotingDto bpmBatchVotingDto = serializeService.toObject(parameters, BPMBatchVotingDto.class);
        bpmBatchVotingService.createBatchVoting(bpmBatchVotingDto, taskId);
        return false;
    }
}
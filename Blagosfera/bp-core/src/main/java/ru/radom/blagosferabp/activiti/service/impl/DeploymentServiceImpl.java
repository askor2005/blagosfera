package ru.radom.blagosferabp.activiti.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.blagosferabp.activiti.component.converters.BpmnConverter;
import ru.radom.blagosferabp.activiti.service.DeploymentService;

import java.util.List;

/**
 * Created by alex on 29.09.2015.<br/>
 * TODO comment me
 */
@Log4j2
@Service
public class DeploymentServiceImpl implements DeploymentService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private BpmnConverter bpmnConverter;


    /*@RabbitListener(queues = BPMBlagosferaUtils.DEPLOY_MODEL)
    public Object deployModelWorker(String modelSource) {
        try {
            deployModel(modelSource);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }*/

    @Override
    @Transactional
    public Deployment deployModel(String modelSource) {
        try {
            final ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(modelSource);
            BpmnModel bpmnModel = bpmnConverter.convertToBpmnModel(modelNode);
            byte[] bpmnBytes = bpmnConverter.convertToXML(bpmnModel);
            org.activiti.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            String id = process.getId();
            if(id == null) {
                throw new IllegalStateException("Model process must have an ID");
            }
            String processName = id + ".bpmn20.xml";

            List<Deployment> deployments = repositoryService.createDeploymentQuery().deploymentName(processName + " deployment").list();
            if (deployments != null) {
                for (Deployment deployment : deployments) {
                    repositoryService.deleteDeployment(deployment.getId(), true);
                }
            }

            Deployment deployment = repositoryService.createDeployment()
                    .name(processName + " deployment")
                    .addString(processName, new String(bpmnBytes, "UTF-8"))
                    .deploy();


            return deployment;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}

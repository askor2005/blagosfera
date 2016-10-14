package ru.radom.blagosferabp.activiti.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.radom.blagosferabp.activiti.component.stencil.StencilSet;
import ru.radom.blagosferabp.activiti.dao.StencilDAO;
import ru.radom.blagosferabp.activiti.dto.util.StencilConverter;
import ru.radom.blagosferabp.activiti.dto.util.StencilFormConverter;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.service.StencilService;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilForm;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilRabbitConstants;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * Реализация для {@link StencilService}
 */
@Service
@Transactional
public class StencilServiceImpl implements StencilService {

    @Autowired
    private StencilConverter stencilConverter;

    @Autowired
    private StencilFormConverter stencilFormConverter;

    @Autowired
    private StencilDAO stencilDAO;

    @Autowired
    private StencilSet defaultStencilSet;

    /**
     * Слушатель очереди {@link StencilRabbitConstants#CREATE_STENCIL}
     */
    //@RabbitListener(queues = StencilRabbitConstants.CREATE_STENCIL)
    public StencilForm createStencil(StencilForm stencil) {
        return stencilFormConverter.convert(create(stencil));
    }

    /**
     * Слушатель очереди {@link StencilRabbitConstants#UPDATE_STENCIL}
     */
    //@RabbitListener(queues = StencilRabbitConstants.UPDATE_STENCIL)
    public StencilForm updateStencil(StencilForm stencil) {
        StencilEntity entity;
        StencilForm result = null;
        try {
            entity = update(stencil);
            result = entity != null ? stencilFormConverter.convert(entity) : null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }

    /**
     * Слушатель очереди {@link StencilRabbitConstants#REMOVE_STENCIL}
     */
    //@RabbitListener(queues = StencilRabbitConstants.REMOVE_STENCIL)
    public void deleteStencil(String stencilId) {
        delete(stencilId);
    }

    /**
     * Слушатель очереди {@link StencilRabbitConstants#GET_CUSTOM_STENCILS}
     */
    //@RabbitListener(queues = StencilRabbitConstants.GET_CUSTOM_STENCILS)
    public List<StencilForm> getCustomStencils() {
        return stencilDAO.findAll().stream().map(stencilFormConverter::convert).collect(Collectors.toList());
    }

    /**
     * Слушатель очереди {@link StencilRabbitConstants#GET_STENCIL_SET}
     */
    //@RabbitListener(queues = StencilRabbitConstants.GET_STENCIL_SET)
    /*public String getStencilSetWorker() throws JsonProcessingException {
        StencilSet stencilSet = getStencilSet();
        return new ObjectMapper().writeValueAsString(stencilSet);
    }*/

    @Override
    @Transactional
    public StencilEntity create(StencilForm stencil) {
        StencilEntity stencilEntity = convertToEntity(stencil);
        stencilDAO.save(stencilEntity);
        return stencilEntity;
    }

    @Override
    @Transactional
    public StencilEntity update(StencilForm stencil) {
        String id = stencil.getId();
        if(id == null) {
            return null;
        }
        StencilEntity entity = stencilDAO.getOne(id);
        if(entity == null) {
            return null;
        }
        copyProperties(stencil, entity);
        stencilDAO.save(entity);
        return entity;
    }

    @Override
    @Transactional
    public void delete(String stencilId) {
        stencilDAO.delete(stencilId);
    }

    @Override
    @Transactional(readOnly = true)
    public StencilSet getStencilSet() {
        List<StencilEntity> all = stencilDAO.findAll();
        StencilSet.StencilSetBuilder builder = defaultStencilSet.toBuilder();
        for (StencilEntity entity : all) {
            builder.stencil(stencilConverter.convert(entity));
        }
        return builder.build();
    }

    private StencilEntity convertToEntity(StencilForm stencil) {
        StencilEntity entity = new StencilEntity();
        copyProperties(stencil, entity);
        return entity;
    }

    private void copyProperties(StencilForm stencil, StencilEntity entity) {
        entity.setTitle(stencil.getTitle());
        entity.setDescription(stencil.getDescription());
        entity.setType(StencilEntity.NODE);
        entity.setIcon(stencil.getIcon());
        entity.setView(stencil.getView());
        entity.setRoles(stencil.getRoles());
        entity.setGroups(stencil.getGroups());
        entity.setQueueToSend(stencil.getQueueToSend());
        entity.setAnswerType(stencil.getAnswerType());

        entity.setProperties(stencil.getProperties());
    }
}

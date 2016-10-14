package ru.radom.blagosferabp.activiti.dto.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilForm;

/**
 * Created by Otts Alexey on 04.11.2015.<br/>
 * {@link StencilEntity} -> {@link StencilForm}
 */
@Component
public class StencilFormConverter implements Converter<StencilEntity, StencilForm> {

    @Override
    public StencilForm convert(StencilEntity source) {
        StencilForm form = new StencilForm();
        form.setId(source.getId());
        form.setDescription(source.getDescription());
        form.setGroups(source.getGroups());
        form.setIcon(source.getIcon());
        form.setTitle(source.getTitle());
        form.setProperties(source.getProperties());
        form.setRoles(source.getRoles());
        form.setView(source.getView());
        form.setQueueToSend(source.getQueueToSend());
        form.setAnswerType(source.getAnswerType());
        return form;
    }
}

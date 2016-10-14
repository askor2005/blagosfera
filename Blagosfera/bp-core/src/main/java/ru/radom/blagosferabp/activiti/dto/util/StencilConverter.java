package ru.radom.blagosferabp.activiti.dto.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.component.stencil.defaultset.properties.DefaultProperties;
import ru.radom.blagosferabp.activiti.model.StencilEntity;
import ru.radom.blagosferabp.activiti.rabbit.bundle.RabbitTaskBundle;
import ru.radom.blagosferabp.activiti.stencil.exchange.Property;
import ru.radom.blagosferabp.activiti.stencil.exchange.Stencil;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Otts Alexey on 03.11.2015.<br/>
 * {@link StencilEntity} -> {@link Stencil}
 */
@Component
public class StencilConverter implements Converter<StencilEntity, Stencil> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DefaultProperties defaultProperties;

    private Set<String> defaultPropertiesIds = new HashSet<>();

    @PostConstruct
    private void postConstruct() {
        defaultPropertiesIds.add(defaultProperties.overrideidpackage().getId());
        defaultPropertiesIds.add(defaultProperties.overrideidpackage().getId());
        defaultPropertiesIds.add(defaultProperties.namepackage().getId());
        defaultPropertiesIds.add(defaultProperties.documentationpackage().getId());
        defaultPropertiesIds.add(defaultProperties.multiinstance_typepackage().getId());
        defaultPropertiesIds.add(defaultProperties.multiinstance_cardinalitypackage().getId());
        defaultPropertiesIds.add(defaultProperties.multiinstance_collectionpackage().getId());
        defaultPropertiesIds.add(defaultProperties.multiinstance_variablepackage().getId());
        defaultPropertiesIds.add(defaultProperties.multiinstance_conditionpackage().getId());
        defaultPropertiesIds.add(defaultProperties.servicetaskresultvariablepackage().getId());
    }

    @Override
    public Stencil convert(StencilEntity source) {
        return Stencil.builder()
                .id(source.getId())
                .title(source.getTitle())
                .description(source.getDescription())
                .type(source.getType())
                .icon(source.getIcon())
                .view(source.getView())
                .mayBeRoot(false)
                .hide(false)
                .roles(simpleExtract(source.getRoles()))
                .groups(simpleExtract(source.getGroups()))
                .properties(extractProperties(source))
                .build();
    }

    public boolean isPropertyDefault(String propertyId) {
        return defaultPropertiesIds.contains(propertyId);
    }

    @SuppressWarnings("unchecked")
    private Collection<Property> extractProperties(StencilEntity source) {
        String properties = source.getProperties();
        try {
            List<Property> result = new ArrayList<>();
            if(properties != null) {
                List list = objectMapper.readValue(properties, new TypeReference<List>() {
                });
                for (Object item : list) {
                    processItem(result, item);
                }
            }
            result.add(stencilEntityIdProperty(source));
            return result;
        } catch (IOException e) {
            return Collections.EMPTY_LIST;
        }
    }

    private Collection<String> simpleExtract(String roles) {
        if(roles == null || roles.isEmpty()) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(roles.trim().split(",\\s*")).stream().map(String::trim).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private void processItem(List<Property> result, Object item) {
        if(item instanceof String) {
            resolveDefaultProperty(result, (String)item);
        } else if(item instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> p = (Map<String, Object>) item;
            String type = MapUtils.getString(p, "type");
            if (type == null) {
                type = Property.STRING;
            }
            Property property = Property.builder()
                .id(MapUtils.getString(p, "id"))
                .title(MapUtils.getString(p, "title"))
                .description(MapUtils.getString(p, "description"))
                .optional(MapUtils.getBoolean(p, "optional", true))
                .min(MapUtils.getInteger(p, "min"))
                .max(MapUtils.getInteger(p, "max"))
                .length(MapUtils.getInteger(p, "length"))
                .items((List) p.get("items"))
                .readonly(MapUtils.getBoolean(p, "readonly", false))
                .value(p.get("value"))
                .popular(MapUtils.getBoolean(p, "popular", true))
                .type(type)
                .items((List<Object>) p.get("items"))
                .build();

            result.add(property);
        }
    }

    private void resolveDefaultProperty(List<Property> result, String item) {
        switch(item.toLowerCase()) {
            case "id": {
                result.add(defaultProperties.overrideidpackage());
                break;
            }
            case "name": {
                result.add(defaultProperties.namepackage());
                break;
            }
            case "documentation": {
                result.add(defaultProperties.documentationpackage());
                break;
            }
            case "multi_instance": {
                result.add(defaultProperties.multiinstance_typepackage());
                result.add(defaultProperties.multiinstance_cardinalitypackage());
                result.add(defaultProperties.multiinstance_collectionpackage());
                result.add(defaultProperties.multiinstance_variablepackage());
                result.add(defaultProperties.multiinstance_conditionpackage());
                break;
            }
            case "result_variable": {
                result.add(defaultProperties.servicetaskresultvariablepackage());
            }
            default:
        }
    }

    private Property stencilEntityIdProperty(StencilEntity source) {
        return Property.builder()
            .id(RabbitTaskBundle.STENCIL_ENTITY_ID_NODE)
            .optional(false)
            .value(source.getId())
            .popular(false)
            .type(Property.STRING)
            .readonly(true)
            .build();
    }

}

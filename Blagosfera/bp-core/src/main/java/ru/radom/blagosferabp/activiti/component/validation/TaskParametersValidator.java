package ru.radom.blagosferabp.activiti.component.validation;

import lombok.SneakyThrows;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.SubProcess;
import org.activiti.validation.ValidationError;
import org.activiti.validation.validator.ProcessLevelValidator;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Otts Alexey on 09.10.2015.<br/>
 */
@Component
public class TaskParametersValidator extends ProcessLevelValidator {

    private Map<Class<? extends FlowElement>, Map<Field, FlowNodeParameter>> classParameters = new HashMap<>();

    public void addTaskClass(Class<? extends FlowElement> taskClass) {
        final Map<Field, FlowNodeParameter> parameters = new HashMap<>();
        ReflectionUtils.doWithFields(taskClass, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                FlowNodeParameter parameter = field.getAnnotation(FlowNodeParameter.class);
                if(parameter != null) {
                    parameters.put(field, parameter);
                }
            }
        });
        if(!parameters.isEmpty()) {
            classParameters.put(taskClass, parameters);
        }
    }

    @Override
    protected void executeValidation(BpmnModel bpmnModel, Process process, List<ValidationError> errors) {
        for (FlowElement element : process.getFlowElements()) {
            processElement(element, errors);
        }
    }

    /**
     * Проверить элемент бизнесс процесса на валидность.
     */
    private void processElement(FlowElement element, List<ValidationError> errors) {
        Map<Field, FlowNodeParameter> parameters = classParameters.get(element.getClass());
        if(parameters != null) {
            for (Map.Entry<Field, FlowNodeParameter> entry : parameters.entrySet()) {
                checkParameter(element, entry.getKey(), entry.getValue(), errors);
            }
        }
        if(element instanceof SubProcess) {
            for (FlowElement subElement : ((SubProcess) element).getFlowElements()) {
                processElement(subElement, errors);
            }
        }
    }

    /**
     * Проверка парамерта на валидность
     */
    @SneakyThrows
    private void checkParameter(FlowElement element, Field field, FlowNodeParameter parameter, List<ValidationError> errors) {
        Object value = PropertyUtils.getProperty(element, field.getName());
        if(value == null) {
            if(parameter.required()) {
                addError(errors, "task.parameter.required", String.format(
                        "Поле '%s' является обязательным!",
                        field.getName()
                ));
            }
        } else {
            if(value instanceof Number) {
                validateNumberValue(((Number) value).doubleValue(), parameter, errors, field);
            } else if(value instanceof String) {
                validateStringValue((String) value, parameter, errors, field);
            }
        }
    }

    private void validateNumberValue(double value, FlowNodeParameter parameter, List<ValidationError> errors, Field field) {
        if(value < parameter.min()) {
            addError(errors, "task.parameter.min", String.format(
                    "Поле '%s' должно быть не меньше %f, но указано %f!",
                    field.getName(),
                    parameter.min(),
                    value
            ));
        }
        if(value > parameter.max()) {
            addError(errors, "task.parameter.max", String.format(
                    "Поле '%s' должно быть не больше %f, но указано %f!",
                    field.getName(),
                    parameter.max(),
                    value
            ));
        }
    }

    private void validateStringValue(String value, FlowNodeParameter parameter, List<ValidationError> errors, Field field) {
        int length = value.length();
        if(length < parameter.minLength()) {
            addError(errors, "task.parameter.min.length", String.format(
                    "Поле '%s' должно быть длиной не меньше %d, но введена строка длиной %d!",
                    field.getName(),
                    parameter.minLength(),
                    length
            ));
        }
        if(length > parameter.maxLength()) {
            addError(errors, "task.parameter.max.length", String.format(
                    "Поле '%s' должно быть длиной не больше %d, но введена строка длиной %d!",
                    field.getName(),
                    parameter.maxLength(),
                    length
            ));
        }
        if(!"".equals(parameter.pattern())) {
            //TODO можно не компилировать каждый раз, если будет проседать производительность
            Pattern pattern = Pattern.compile(parameter.pattern());
            Matcher matcher = pattern.matcher(value);
            if(!matcher.matches()) {
                addError(errors, "task.parameter.pattern", String.format(
                        "Строка, находящаяся в поле '%s', должна соответсвовать регулярному выражению '%s', но используется: %s!",
                        field.getName(),
                        parameter.pattern(),
                        value
                ));
            }
        }
    }


}

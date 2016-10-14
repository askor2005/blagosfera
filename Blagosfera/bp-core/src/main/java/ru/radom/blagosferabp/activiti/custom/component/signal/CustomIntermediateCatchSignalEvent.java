package ru.radom.blagosferabp.activiti.custom.component.signal;

import lombok.Getter;
import lombok.Setter;
import org.activiti.bpmn.model.IntermediateCatchEvent;
import ru.radom.blagosferabp.activiti.component.reflection.CustomIntermediateCatchEvent;
import ru.radom.blagosferabp.activiti.component.reflection.FlowNodeParameter;

import static ru.radom.blagosferabp.activiti.custom.component.signal.CustomIntermediateCatchSignalEventBundle.*;

/**
 * Created by Otts Alexey on 30.11.2015.<br/>
 * Реализация {@link IntermediateCatchEvent} в которой можно задавать сигналы динамически
 */
@Getter
@Setter
@CustomIntermediateCatchEvent(
    bundle = CustomIntermediateCatchSignalEventBundle.class,
    type = CUSTOM_SIGNAL_EVENT
)
public class CustomIntermediateCatchSignalEvent extends IntermediateCatchEvent {

    /**
     * Кастомный сигнал
     */
    @FlowNodeParameter(
        stencilParameter = CUSTOM_SIGNAL_ATTR,
        xmlParameter = CUSTOM_SIGNAL_ATTR,
        required = true
    )
    private String customSignal;
}

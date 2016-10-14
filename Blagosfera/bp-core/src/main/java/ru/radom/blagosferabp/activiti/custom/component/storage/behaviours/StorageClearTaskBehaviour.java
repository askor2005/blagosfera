package ru.radom.blagosferabp.activiti.custom.component.storage.behaviours;

import org.activiti.engine.delegate.Expression;
import org.activiti.engine.impl.bpmn.behavior.TaskActivityBehavior;
import org.activiti.engine.impl.pvm.delegate.ActivityExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.radom.blagosferabp.activiti.service.StoredKeyValueService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Otts Alexey on 23.11.2015.<br/>
 * Поведение для {@link ru.radom.blagosferabp.activiti.custom.component.storage.StorageClearTask}
 */
@Component
@Scope("prototype")
public class StorageClearTaskBehaviour extends TaskActivityBehavior {

    @Autowired
    private StoredKeyValueService storedKeyValueService;

    /**
     * Ключи, которые нужно удалить
     */
    List<Expression> keys;

    public StorageClearTaskBehaviour(List<Expression> keys) {
        this.keys = keys;
    }

    @Override
    public void execute(ActivityExecution execution) throws Exception {
        List<String> keys = (List<String>)this.keys.stream()
            .map(k -> k.getValue(execution))
            .filter(Objects::nonNull)
            .flatMap(v -> {
                if (v instanceof List) {
                    return ((List) v).stream().map(String::valueOf);
                }
                if (v instanceof Map) {
                    return ((Map) v).keySet().stream().map(String::valueOf);
                }
                return Stream.of(String.valueOf(v));
            })
            .distinct()
            .collect(Collectors.toList());

        storedKeyValueService.clearAll(keys);
        leave(execution);
    }

}

package ru.radom.blagosferabp.activiti.rabbit;

import java.util.Map;

/**
 *
 * Created by vgusev on 29.07.2016.
 */
public interface BPMHandler {

    Object handle(Map<String, Object> parameters, String taskId);
}

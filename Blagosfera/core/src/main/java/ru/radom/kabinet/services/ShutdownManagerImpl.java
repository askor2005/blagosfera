package ru.radom.kabinet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;
import ru.radom.kabinet.services.schedule.ScheduleManager;

@Component("shutdownManager")
public class ShutdownManagerImpl implements ShutdownManager, ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ScheduleManager scheduleManager;

    public ShutdownManagerImpl() {
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        scheduleManager.shutdown();
    }
}

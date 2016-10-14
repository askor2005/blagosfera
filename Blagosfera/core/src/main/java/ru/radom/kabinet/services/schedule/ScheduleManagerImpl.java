package ru.radom.kabinet.services.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.core.services.robokassa.RobokassaService;
import ru.radom.kabinet.services.ChatService;
import ru.radom.kabinet.services.CyberbrainService;
import ru.radom.kabinet.services.ProfileService;
import ru.radom.kabinet.services.letterOfAuthority.LetterOfAuthorityService;
import ru.radom.kabinet.services.payment.PaymentService;

@Transactional
@Service("scheduleManager")
@DependsOn("transactionManager")
public class ScheduleManagerImpl implements ScheduleManager {

    @Autowired
    @Qualifier("taskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Autowired
    @Qualifier("taskScheduler")
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private CyberbrainService cyberbrainService;

    @Autowired
    private LetterOfAuthorityService letterOfAuthorityService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RobokassaService robokassaService;

    public ScheduleManagerImpl() {
    }

    @Override
    public void shutdown() {
        scheduler.shutdown();
        executor.shutdown();
    }

    @Scheduled(cron = "0 0 5,17 * * *")
    public void checkSharers() {
        profileService.checkSharers();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkFileMessages() {
        chatService.checkFileMessages();
    }

    // @Scheduled(cron = "0 * * * * *") - запуск раз в минуту
    // @Scheduled(cron = "0 0/5 0,2-23 * * ?") - запуск раз в 5 минут
    // @Scheduled(cron = "0 0 1 * * ?") - запуск раз в день в 1:00 ночи
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateAffectsForTracks() {
        cyberbrainService.updateAffectsForTracks();
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkLetterOfAuthorities() {
        letterOfAuthorityService.checkLetterOfAuthorities();
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void checkPayments() {
        paymentService.checkPayments();
        robokassaService.checkPayments();
    }
}

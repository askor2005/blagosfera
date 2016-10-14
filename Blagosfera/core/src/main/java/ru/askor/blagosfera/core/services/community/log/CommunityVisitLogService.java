package ru.askor.blagosfera.core.services.community.log;

/**
 * Created by vtarasenko on 14.07.2016.
 */
public interface CommunityVisitLogService {
    void createCommunityVisitLog(String seolink, Long userId);
}

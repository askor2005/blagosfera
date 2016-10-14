package ru.radom.kabinet.services.communities;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSettingCustomSourceHandler;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

/**
 *
 * Created by vgusev on 19.07.2016.
 */
@Service("communityDocumentCustomSourceHandler")
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CommunityDocumentCustomSourceHandler implements DocumentTemplateSettingCustomSourceHandler {

    private Long newMemberUserId;

    private Long communityId;

    public CommunityDocumentCustomSourceHandler() {}

    public CommunityDocumentCustomSourceHandler(Long newMemberUserId, Long communityId) {
        this.newMemberUserId = newMemberUserId;
        this.communityId = communityId;
    }

    @Override
    public Long handleCustomSource(String sourceName) {
        Long result = null;
        switch (sourceName) {
            case "user":
                result = newMemberUserId;
                break;
            case "community":
                result = communityId;
                break;
        }
        ExceptionUtils.check(result == null, "Не найден код участника шаблона документа");
        return result;
    }

}

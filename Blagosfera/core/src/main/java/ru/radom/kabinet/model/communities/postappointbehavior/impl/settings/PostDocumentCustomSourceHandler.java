package ru.radom.kabinet.model.communities.postappointbehavior.impl.settings;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.askor.blagosfera.domain.document.templatesettings.DocumentTemplateSettingCustomSourceHandler;
import ru.radom.kabinet.utils.exception.ExceptionUtils;

/**
 *
 * Created by vgusev on 02.08.2016.
 */
@Service("postDocumentCustomSourceHandler")
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PostDocumentCustomSourceHandler implements DocumentTemplateSettingCustomSourceHandler {

    private Long userId;

    private Long communityId;

    public PostDocumentCustomSourceHandler() {}

    public PostDocumentCustomSourceHandler(Long userId, Long communityId) {
        this.userId = userId;
        this.communityId = communityId;
    }

    @Override
    public Long handleCustomSource(String sourceName) {
        Long result = null;
        switch (sourceName) {
            case "user":
                result = userId;
                break;
            case "community":
                result = communityId;
                break;
        }
        ExceptionUtils.check(result == null, "Не найден код участника шаблона документа");
        return result;
    }

}

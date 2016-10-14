package ru.radom.kabinet.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import ru.radom.kabinet.collections.CommunityPermissionsList;
import ru.radom.kabinet.collections.CommunityPostsList;
import ru.radom.kabinet.collections.RolesList;
import ru.radom.kabinet.collections.SharersList;
import ru.radom.kabinet.dao.DaoManager;
import ru.radom.kabinet.model.LongIdentifiable;
import ru.radom.kabinet.web.mapping.LongIdentifiablePropertyEditor;
import ru.radom.kabinet.web.mapping.SharersSetPropertyEditor;
import ru.radom.kabinet.web.mapping.collections.CommunityPermissionsListPropertyEditor;
import ru.radom.kabinet.web.mapping.collections.CommunityPostsListPropertyEditor;
import ru.radom.kabinet.web.mapping.collections.RolesListPropertyEditor;
import ru.radom.kabinet.web.mapping.collections.SharersListPropertyEditor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@ControllerAdvice
@DependsOn("daoManager")
public class ControllerBindingAdvice implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerBindingAdvice.class);

    private final Map<Class<? extends LongIdentifiable>, PropertyEditorSupport> PROPERTY_EDITORS_MAP = new HashMap<Class<? extends LongIdentifiable>, PropertyEditorSupport>();

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    private DaoManager daoManager;

    @InitBinder
    public void initBinderAll(WebDataBinder binder) {
        try {
            for (Entry<Class<? extends LongIdentifiable>, PropertyEditorSupport> entry : PROPERTY_EDITORS_MAP.entrySet()) {
                binder.registerCustomEditor(entry.getKey(), entry.getValue());
            }

            binder.registerCustomEditor(RolesList.class, applicationContext.getBean(RolesListPropertyEditor.class));
            binder.registerCustomEditor(SharersList.class, applicationContext.getBean(SharersListPropertyEditor.class));
            //binder.registerCustomEditor(DocumentsList.class, applicationContext.getBean(DocumentsListPropertyEditor.class));
            binder.registerCustomEditor(CommunityPermissionsList.class, applicationContext.getBean(CommunityPermissionsListPropertyEditor.class));
            binder.registerCustomEditor(CommunityPostsList.class, applicationContext.getBean(CommunityPostsListPropertyEditor.class));

            binder.registerCustomEditor(Set.class, "participants", applicationContext.getBean(SharersSetPropertyEditor.class));
        } catch (NoSuchBeanDefinitionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Class<? extends LongIdentifiable> clazz : daoManager.getPersistentClasses()) {
            PROPERTY_EDITORS_MAP.put(clazz, new LongIdentifiablePropertyEditor(daoManager.getDao(clazz)));
            LOGGER.info("property editor for " + clazz.getName() + " created");
        }
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void processAcessDenied(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), exception.getMessage());
    }
}

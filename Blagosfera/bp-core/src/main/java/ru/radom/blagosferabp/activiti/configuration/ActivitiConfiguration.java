package ru.radom.blagosferabp.activiti.configuration;

import lombok.extern.log4j.Log4j2;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.engine.*;
import org.activiti.engine.parse.BpmnParseHandler;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringCallerRunsRejectedJobsHandler;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.SpringRejectedJobsHandler;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ProcessValidatorImpl;
import org.activiti.validation.validator.ValidatorSet;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;
import ru.radom.blagosferabp.activiti.component.CustomFlowNodeRegistry;
import ru.radom.blagosferabp.activiti.component.ModelBundle;
import ru.radom.blagosferabp.activiti.component.stencil.StencilSet;
import ru.radom.blagosferabp.activiti.component.stencil.StencilsRegistry;
import ru.radom.blagosferabp.activiti.component.validation.TaskParametersValidator;
import ru.radom.blagosferabp.activiti.service.JuelUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alex on 16.09.2015.<br/>
 * Configuration of Activiti
 */
@Log4j2
@Configuration
public class ActivitiConfiguration {

    @Autowired
    private TaskParametersValidator taskParametersValidator;

    @Bean
    public SpringRejectedJobsHandler springRejectedJobsHandler() {
        return new SpringCallerRunsRejectedJobsHandler();
    }

    @Bean
    public ProcessEngineFactoryBean processEngine(SpringProcessEngineConfiguration configuration) throws Exception {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(configuration);
        return processEngineFactoryBean;
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public TaskService taskServiceBean(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public HistoryService historyServiceBean(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public ManagementService managementServiceBean(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public FormService formServiceBean(ProcessEngine processEngine) {
        return processEngine.getFormService();
    }

    @Bean
    @Conditional(ClassNotRegisteredYet.class)
    public IdentityService identityServiceBean(ProcessEngine processEngine) {
        return processEngine.getIdentityService();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }


    @Bean
    @DependsOn("bpmnConverter")
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            final PlatformTransactionManager transactionManager,
            final DataSource bpDataSource,
            final CustomFlowNodeRegistry customFlowNodeRegistry,
            final ApplicationContext applicationContext,
            final JuelUtils juelUtils
    ) throws IOException {
        Properties properties = PropertiesLoaderUtils.loadAllProperties("activiti-conf.properties");

        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(bpDataSource);
        configuration.setTransactionManager(transactionManager);
        configuration.setJobExecutorActivate(true);
        setDbSchemaUpdate(configuration, properties);
        setUpResources(configuration, properties);
        setUpPreParsers(configuration, customFlowNodeRegistry);
        List<Class<? extends FlowNode>> classes = customFlowNodeRegistry.getBundlesByClass().values().stream()
            .flatMap(b -> b.keySet().stream())
            .collect(Collectors.toList());
        setUpProcessValidator(configuration, classes);
        setUpServices(configuration, applicationContext);
        configuration.setBeans(Collections.singletonMap("utils", juelUtils));
        return configuration;
    }

    private void setUpServices(SpringProcessEngineConfiguration configuration, ApplicationContext applicationContext) {
        IdentityService identityService = tryGetBean(applicationContext, IdentityService.class);
        if(identityService != null) {
            configuration.setIdentityService(identityService);
        }

        HistoryService historyService = tryGetBean(applicationContext, HistoryService.class);
        if(historyService != null) {
            configuration.setHistoryService(historyService);
        }

        RepositoryService repositoryService = tryGetBean(applicationContext, RepositoryService.class);
        if(repositoryService != null) {
            configuration.setRepositoryService(repositoryService);
        }

        TaskService taskService = tryGetBean(applicationContext, TaskService.class);
        if(taskService != null) {
            configuration.setTaskService(taskService);
        }

        ManagementService managementService = tryGetBean(applicationContext, ManagementService.class);
        if(managementService != null) {
            configuration.setManagementService(managementService);
        }

        FormService formService = tryGetBean(applicationContext, FormService.class);
        if(formService != null) {
            configuration.setFormService(formService);
        }
    }

    private <T> T tryGetBean(ApplicationContext applicationContext, Class<? extends T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException | BeanCreationException e) {
            return null;
        }
    }

    private void setDbSchemaUpdate(SpringProcessEngineConfiguration configuration, Properties properties) {
        String dbSchemaUpdate = SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE;
        String property = properties.getProperty("activiti.db.schema.update");
        if (property != null && !Boolean.parseBoolean(property)) {
            dbSchemaUpdate = SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE;
        }
        configuration.setDatabaseSchemaUpdate(dbSchemaUpdate);
    }

    private void setUpPreParsers(SpringProcessEngineConfiguration configuration, CustomFlowNodeRegistry customFlowNodeRegistry) {
        if (!customFlowNodeRegistry.getBundlesByClass().isEmpty()) {
            List<BpmnParseHandler> parseHandlers = new ArrayList<>();
            for (Map<Class<? extends FlowNode>, ModelBundle> map : customFlowNodeRegistry.getBundlesByClass().values()) {
                for (ModelBundle modelBundle : map.values()) {
                    BpmnParseHandler parseHandler = modelBundle.getBpmnParseHandler();
                    if (parseHandler != null) {
                        parseHandlers.add(parseHandler);
                    }
                }
            }
            configuration.setPreBpmnParseHandlers(parseHandlers);
        }
    }

    private void setUpResources(SpringProcessEngineConfiguration configuration, Properties properties) throws IOException {
        String resourcesPaths = properties.getProperty("activiti.process.resources");
        if(resourcesPaths != null) {
            Resource[] resources = new Resource[]{};
            for (String s : resourcesPaths.split("\\|\\|")) {
                if(StringUtils.hasText(s)) {
                    PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
                    resources = ArrayUtils.addAll(
                            resources,
                            pathMatchingResourcePatternResolver.getResources(s.trim())
                    );
                }
            }
            configuration.setDeploymentResources(resources);
        }
    }

    private void setUpProcessValidator(
            SpringProcessEngineConfiguration configuration,
            Collection<Class<? extends FlowNode>> classes
    ) {
        ValidatorSet customSet = new ValidatorSet("custom-validator-set");
        customSet.addValidator(taskParametersValidator);

        for (Class<? extends FlowNode> taskClass : classes) {
            taskParametersValidator.addTaskClass(taskClass);
        }

        ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
        if(validator instanceof ProcessValidatorImpl) {
            ((ProcessValidatorImpl) validator).addValidatorSet(customSet);
        }
        configuration.setProcessValidator(validator);
    }

    @Bean
    public StencilSet defaultStencilSet(final StencilsRegistry stencilsRegistry) {
        return stencilsRegistry.createSet()
                .title("Process editor")
                .namespace("http://b3mn.org/stencilset/bpmn2.0#")
                .description("BPMN process editor")
                .build();
    }

}

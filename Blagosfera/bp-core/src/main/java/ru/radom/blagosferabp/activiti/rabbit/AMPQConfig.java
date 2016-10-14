package ru.radom.blagosferabp.activiti.rabbit;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.radom.blagosferabp.activiti.BPMBlagosferaUtils;
import ru.radom.blagosferabp.activiti.stencil.exchange.StencilRabbitConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Otts Alexey on 02.11.2015.<br/>
 * Настройки очередей RabbitMQ
 */
@Log4j2
@EnableRabbit
@Configuration
public class AMPQConfig {

    private final Long expire = 60000L;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Bean
    public Queue stencilCreatedQueue() {
        return new Queue(StencilRabbitConstants.STENCIL_CREATED, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue stencilRemovedQueue() {
        return new Queue(StencilRabbitConstants.STENCIL_REMOVED, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue stencilUpdatedQueue() {
        return new Queue(StencilRabbitConstants.STENCIL_UPDATED, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue createStencilQueue() {
        return new Queue(StencilRabbitConstants.CREATE_STENCIL, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue updateStencilQueue() {
        return new Queue(StencilRabbitConstants.UPDATE_STENCIL, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue removeStencilQueue() {
        return new Queue(StencilRabbitConstants.REMOVE_STENCIL, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue getCustomStencilsQueue() {
        return new Queue(StencilRabbitConstants.GET_CUSTOM_STENCILS, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue getStencilQueue() {
        return new Queue(StencilRabbitConstants.GET_STENCIL, false, false, false, argsWithExpire(expire));
    }
    @Bean
    public Queue getStencilSetQueue() {
        return new Queue(StencilRabbitConstants.GET_STENCIL_SET, false, false, false, argsWithExpire(expire));
    }

    @Bean
    public Queue loggerQueue() {
        return new Queue("bp.logger", false, false, false, argsWithExpire(expire));
    }

    /*@Bean
    public Queue finishTaskQueue() {
        return new Queue(BPMBlagosferaUtils.FINISH_TASK_QUEUE, true, false, false);
    }*/
    /*@Bean
    public Queue signalActivitiQueue() {
        return new Queue(BPMBlagosferaUtils.SIGNAL_QUEUE, false, false, false);
    }*/
    /*@Bean
    public Queue deployModelQueue() {
        return new Queue(BPMBlagosferaUtils.DEPLOY_MODEL, false, false, false);
    }*/

    @RabbitListener(queues = "bp.logger")
    public void logger(Message msg) {
        log.info(msg);
    }

    private Map<String, Object> argsWithExpire(Long expire) {
        Map<String, Object> args = new HashMap<>(1);
        args.put("x-message-ttl", expire);
        return args;
    }

}

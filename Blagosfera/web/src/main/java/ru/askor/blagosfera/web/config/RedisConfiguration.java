package ru.askor.blagosfera.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

/**
 * Created by vtarasenko on 28.04.2016.
 */
@Configuration
@EnableRedisRepositories(basePackages = {"ru.askor.blagosfera.data.redis.repositories"},redisTemplateRef = "redisCacheTemplate")
public class RedisConfiguration {
}

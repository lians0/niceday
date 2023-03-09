package com.niceday.tools.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ConditionalOnClass(NiceLog.class)
public class NiceLogAutoConfiguration {
    @Bean
    public NiceLogAspect methodLoggerAspect() {
        return new NiceLogAspect();
    }
}

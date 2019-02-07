package io.electrica.metric.connection.invocation.event.config;

import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationEvent;
import io.electrica.metric.common.mq.connection.invocation.event.ConnectionInvocationResultEvent;
import io.electrica.metric.connection.invocation.dto.ConnectionInvocationDto;
import io.electrica.metric.connection.invocation.model.ConnectionInvocation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.dozermapper.core.loader.api.FieldsMappingOptions.copyByReference;

@Configuration
public class ConnectionInvocationDozerConfig {
    @Bean
    public BeanMappingBuilder userServiceMappingBuilder() {
        return new BeanMappingBuilder() {
            @Override
            protected void configure() {
                mapping(ConnectionInvocationEvent.class, ConnectionInvocationDto.class)
                        .fields("parameters", "parameters", copyByReference())
                        .fields("payload", "payload", copyByReference());

                mapping(ConnectionInvocationResultEvent.class, ConnectionInvocationDto.class)
                        .fields("result", "result", copyByReference());

                mapping(ConnectionInvocationDto.class, ConnectionInvocation.class)
                        .fields("parameters", "parameters", copyByReference())
                        .fields("payload", "payload", copyByReference())
                        .fields("result", "result", copyByReference());
            }
        };
    }
}

package io.electrica.common.config;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Add to container Dozer {@link Mapper}, configured using {@link BeanMappingBuilder} set.
 * <p>
 * To configure just add to container in your module appropriate {@link BeanMappingBuilder}.
 */
@Configuration
public class DozerMapperConfig {

    @Autowired(required = false)
    private List<BeanMappingBuilder> bmbs;

    @Bean
    public Mapper dozerBeanMapper() {
        DozerBeanMapperBuilder builder = DozerBeanMapperBuilder.create();
        if (bmbs != null) {
            for (BeanMappingBuilder bmb : bmbs) {
                builder.withMappingBuilder(bmb);
            }
        }
        return builder.build();
    }

//    new BeanMappingBuilder() {
//        @Override
//        protected void configure() {
//            mapping(VehicleDto.class, Vehicle.class, wildcard(false), oneWay())
//                    .fields("id", "id")
//                    .fields("version", "version")
//                    .fields("name", "name")
//                    .fields("carrierId", "carrier.id");
//        }
//    }
}


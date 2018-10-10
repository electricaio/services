package io.electrica.common.jpa.service.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.Lob;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
class EntityStringFieldsCache {

    private static final Logger log = LoggerFactory.getLogger(EntityStringFieldsCache.class);

    private final ConcurrentMap<Class, List<StringPropertyMetadata>> stringPropertiesByType = new ConcurrentHashMap<>();

    private static List<StringPropertyMetadata> findStringProperties(Class type) {
        List<StringPropertyMetadata> result = new ArrayList<>();
        ReflectionUtils.doWithFields(
                type,
                // Field callback
                field -> {
                    StringPropertyMetadata metadata = mapToMetadata(type, field);
                    if (metadata != null) {
                        result.add(metadata);
                    }
                },
                // Filter callback
                field -> !field.getType().isInterface()
                        && field.getType().isAssignableFrom(String.class)
                        && field.getAnnotation(Column.class) != null
                        && field.getAnnotation(Lob.class) == null
        );
        return result;
    }

    private static StringPropertyMetadata mapToMetadata(Class<?> clazz, Field field) {
        String name = field.getName();
        String getterName = "get" + StringUtils.capitalize(name);
        Method getter = ReflectionUtils.findMethod(clazz, getterName);
        if (getter == null) {
            log.error("Unable to find {} in class {} or it's superclasses", getterName, clazz.getName());
            return null;
        }
        Column columnAnnotation = field.getAnnotation(Column.class);
        return new StringPropertyMetadata(name, columnAnnotation.length(), columnAnnotation.nullable(), getter);
    }

    List<StringPropertyMetadata> getStringPropertiesMetadata(Class entityType) {
        return stringPropertiesByType.computeIfAbsent(
                entityType,
                EntityStringFieldsCache::findStringProperties
        );
    }

    @Getter
    @AllArgsConstructor
    static class StringPropertyMetadata {
        private final String propertyName;
        private final Integer length;
        private final Boolean nullable;
        private final Method method;
    }
}

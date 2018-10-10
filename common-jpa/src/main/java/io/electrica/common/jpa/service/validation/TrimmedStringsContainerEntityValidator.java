package io.electrica.common.jpa.service.validation;

import io.electrica.common.exception.BadRequestServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public class TrimmedStringsContainerEntityValidator implements ContainerEntityValidator {

    private static final Logger log = LoggerFactory.getLogger(TrimmedStringsContainerEntityValidator.class);

    private final EntityStringFieldsCache entityStringFieldsCache;

    @Inject
    public TrimmedStringsContainerEntityValidator(EntityStringFieldsCache entityStringFieldsCache) {
        this.entityStringFieldsCache = entityStringFieldsCache;
    }

    @Override
    public String getId() {
        return TRIMMED_STRINGS;
    }

    @Override
    public void validateCreate(Object create) {
        List<EntityStringFieldsCache.StringPropertyMetadata> propertiesMetadata =
                entityStringFieldsCache.getStringPropertiesMetadata(create.getClass());

        for (EntityStringFieldsCache.StringPropertyMetadata metadata : propertiesMetadata) {
            String propertyName = metadata.getPropertyName();
            try {
                String propertyValue = (String) metadata.getMethod().invoke(create);
                if (propertyValue != null) {
                    String trimmed = StringUtils.trim(propertyValue);
                    if (trimmed.length() != propertyValue.length()) {
                        String message = String.format("%s must be trimmed", propertyName);
                        throw new BadRequestServiceException(message);
                    }
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                String message = String.format("Unable to get value of parameter %s", propertyName);
                log.error(message, e);
            }
        }
    }
}

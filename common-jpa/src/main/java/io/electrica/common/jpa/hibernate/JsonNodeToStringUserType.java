package io.electrica.common.jpa.hibernate;

import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.HibernateException;

public class JsonNodeToStringUserType extends ObjectToJsonStringUserType {
    public JsonNodeToStringUserType() {
        super(JsonNode.class);
    }

    @Override
    public Object deepCopy(Object originalValue) throws HibernateException {
        if (originalValue instanceof JsonNode) {
            return ((JsonNode) originalValue).deepCopy();
        }
        return null;
    }
}

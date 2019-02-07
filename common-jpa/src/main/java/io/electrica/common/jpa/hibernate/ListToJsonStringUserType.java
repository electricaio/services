package io.electrica.common.jpa.hibernate;

import org.hibernate.HibernateException;

import java.util.ArrayList;
import java.util.List;

public class ListToJsonStringUserType extends ObjectToJsonStringUserType {
    public ListToJsonStringUserType() {
        super(List.class);
    }

    @Override
    public Object deepCopy(Object originalValue) throws HibernateException {
        if (originalValue instanceof List) {
            return new ArrayList<>((List<?>) originalValue);
        }
        return null;
    }
}

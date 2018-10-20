package io.electrica.stl.util;

import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.STLTypeRepository;

public interface Fixture {

    default STLType createSTLType(String name) {
        final STLType model = new STLType();
        model.setName(name);
        return getSTLTypeRepository().saveAndFlush(model);
    }

    STLTypeRepository getSTLTypeRepository();
}

package io.electrica.services.stl.service.memory;

import io.electrica.services.stl.model.AbstractBaseEntity;
import io.electrica.services.stl.service.CrudService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class AbstractInMemoryService<T extends AbstractBaseEntity> implements CrudService<T> {

    private final Map<Long, T> map = new LinkedHashMap<>();

    private final AtomicLong sequence = new AtomicLong(1);

    public T findOne(Long id) {
        return map.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(map.values());
    }

    public T save(T t) {
        if (t.getId() == null) {
            t.setId(sequence.getAndIncrement());
        }
        map.put(t.getId(), t);
        return t;
    }

    public void remove(Long id) throws IllegalArgumentException {
        T t = map.remove(id);
        if (t == null) {
            throw new IllegalArgumentException(String.format("Error: Tried to remove non-existing entity with id=%d.", id));
        }
    }
}

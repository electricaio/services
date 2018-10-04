package io.electrica.services.stl.service;

import io.electrica.services.stl.model.AbstractBaseEntity;

import java.util.List;

public interface CrudService<T extends AbstractBaseEntity> {

    /**
     * Find and return entity with passed id.
     *
     * @param id of the entity to return
     * @return entity with passed id or null if not found
     */
    T findOne(Long id);

    /**
     * Return back all existing entities.
     *
     * @return list of existing entities, empty list if there are no entities
     */
    List<T> findAll();

    /**
     * Save entity and return saved instance (with id set).
     *
     * @param entity to be saved
     * @return saved instance
     */
    T save(T entity);

    /**
     * Remove entity with passed id.
     *
     * @param id of the entity to be removed
     * @throws IllegalArgumentException if there is no entity with passed id
     */
    void remove(Long id) throws IllegalArgumentException;
}


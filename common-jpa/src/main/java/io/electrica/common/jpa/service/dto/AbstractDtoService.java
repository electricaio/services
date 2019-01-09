package io.electrica.common.jpa.service.dto;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.model.CommonEntity;
import io.electrica.common.jpa.service.AbstractService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractDtoService<P extends CommonEntity, C, D> {

    @Inject
    protected Mapper mapper;

    public D findById(long id, boolean hideArchived) {
        return toDto(getService().findById(id, hideArchived));
    }

    public D create(C persistentDto) {
        return toDto(getService().create(toCreateEntity(persistentDto)));
    }

    public <U> D update(long id, U persistentDto) {
        return toDto(getService().update(id, toUpdateEntity(persistentDto)));
    }

    public <U> P toUpdateEntity(U dto) {
        return mapper.map(dto, getEntityClass());
    }

    public D archive(long id) {
        return toDto(getService().archive(id));
    }

    public D unArchive(long id) {
        return toDto(getService().unArchive(id));
    }

    public P toEntity(D dto) {
        return mapper.map(dto, getEntityClass());
    }

    public P toCreateEntity(C dto) {
        return mapper.map(dto, getEntityClass());
    }

    public D toDto(P entity) {
        return mapper.map(entity, getDtoClass());
    }

    public List<D> toDto(List<P> fromList) {
        return fromList
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    protected abstract AbstractService<P> getService();

    protected abstract Class<P> getEntityClass();

    protected abstract Class<D> getDtoClass();

}

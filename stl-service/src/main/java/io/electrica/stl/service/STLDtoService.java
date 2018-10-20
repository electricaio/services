package io.electrica.stl.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.dto.AbstractDtoService;
import io.electrica.stl.model.STL;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class STLDtoService extends AbstractDtoService<STL, CreateSTLDto, ReadSTLDto> {

    private final STLService stlService;

    public STLDtoService(Mapper mapper, STLService stlService) {
        this.mapper = mapper;
        this.stlService = stlService;
    }

    public List<ReadSTLDto> findAll() {
        return toDto(stlService.findAll());
    }

    @Override
    protected AbstractService<STL> getService() {
        return stlService;
    }

    @Override
    protected Class<STL> getEntityClass() {
        return STL.class;
    }

    @Override
    protected Class<ReadSTLDto> getDtoClass() {
        return ReadSTLDto.class;
    }
}

package io.electrica.stl.api;

import com.github.dozermapper.core.Mapper;
import io.electrica.stl.model.STL;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.STLService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class STLApi {

    private final Mapper mapper;

    private final STLService stlService;

    public STLApi(Mapper mapper, STLService stlService) {
        this.mapper = mapper;
        this.stlService = stlService;
    }

    public ReadSTLDto create(CreateSTLDto request) {
        final STL model = mapper.map(request, STL.class);
        stlService.create(model);
        return mapper.map(model, ReadSTLDto.class);
    }

    public List<ReadSTLDto> findAll() {
        return stlService.findAll()
                .stream()
                .map(e -> mapper.map(e, ReadSTLDto.class))
                .collect(Collectors.toList());
    }
}

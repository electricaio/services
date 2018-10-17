package io.electrica.stl.api;

import io.electrica.stl.model.STL;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.STLService;
import io.electrica.stl.service.mapper.STLMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class STLApi {

    private final STLMapper stlMapper;

    private final STLService stlService;

    public STLApi(STLMapper stlMapper, STLService stlService) {
        this.stlMapper = stlMapper;
        this.stlService = stlService;
    }

    public ReadSTLDto create(CreateSTLDto request) {
        final STL model = stlMapper.toEntity(request);
        stlService.create(model);
        return stlMapper.toDto(model);
    }

    public List<ReadSTLDto> findAll() {
        return stlService.findAll()
                .stream()
                .map(stlMapper::toDto)
                .collect(Collectors.toList());
    }
}

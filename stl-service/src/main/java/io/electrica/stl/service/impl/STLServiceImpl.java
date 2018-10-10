package io.electrica.stl.service.impl;

import com.github.dozermapper.core.Mapper;
import io.electrica.stl.model.STL;
import io.electrica.stl.repository.STLRepository;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import io.electrica.stl.service.ERNService;
import io.electrica.stl.service.STLService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class STLServiceImpl implements STLService {

    private final Mapper mapper;

    private final STLRepository stlRepository;

    private final ERNService ernService;

    public STLServiceImpl(Mapper mapper, STLRepository stlRepository, ERNService ernService) {
        this.mapper = mapper;
        this.stlRepository = stlRepository;
        this.ernService = ernService;
    }

    @Override
    public List<ReadSTLDto> list() {
        return stlRepository.findAll()
                .stream()
                .map(e -> mapper.map(e, ReadSTLDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReadSTLDto create(CreateSTLDto request) {
        final STL model = mapper.map(request, STL.class);

        final String ern = ernService.assignERN(model.getName());
        model.setErn(ern);

        stlRepository.save(model);

        return mapper.map(model, ReadSTLDto.class);
    }
}

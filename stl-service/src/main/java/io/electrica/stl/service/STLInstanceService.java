package io.electrica.stl.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLInstance;
import io.electrica.stl.repository.STLInstanceRepository;
import io.electrica.stl.repository.STLRepository;
import io.electrica.stl.rest.dto.CreateSTLInstanceDto;
import io.electrica.stl.rest.dto.ReadSTLInstanceDto;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Component
public class STLInstanceService {

    private final STLRepository stlRepository;

    private final STLInstanceRepository stlInstanceRepository;

    private final Mapper mapper;

    public STLInstanceService(STLRepository stlRepository,
                              STLInstanceRepository stlInstanceRepository,
                              Mapper mapper) {
        this.stlRepository = stlRepository;
        this.stlInstanceRepository = stlInstanceRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ReadSTLInstanceDto create(CreateSTLInstanceDto dto) {

        final STL stl = stlRepository.findById(dto.getStlId())
                .orElseThrow(EntityNotFoundException::new);

        final STLInstance model = new STLInstance(stl, dto.getAccessKeyId());
        stlInstanceRepository.save(model);

        return mapper.map(model, ReadSTLInstanceDto.class);
    }
}

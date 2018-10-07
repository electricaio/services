package io.electrica.stl.service.impl;

import com.github.dozermapper.core.Mapper;
import io.electrica.stl.repository.STLRepository;
import io.electrica.stl.rest.dto.STLDto;
import io.electrica.stl.service.STLService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class STLServiceImpl implements STLService {

    private Mapper mapper;

    private STLRepository stlRepository;

    public STLServiceImpl(Mapper mapper, STLRepository stlRepository) {
        this.mapper = mapper;
        this.stlRepository = stlRepository;
    }

    @Override
    public List<STLDto> findAll() {
        return stlRepository.findAll()
                .stream()
                .map(e -> mapper.map(e, STLDto.class))
                .collect(Collectors.toList());
    }
}

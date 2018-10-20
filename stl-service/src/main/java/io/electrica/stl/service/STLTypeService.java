package io.electrica.stl.service;

import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.STLTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class STLTypeService {

    private final STLTypeRepository stlTypeRepository;

    public STLTypeService(STLTypeRepository stlTypeRepository) {
        this.stlTypeRepository = stlTypeRepository;
    }

    public List<STLType> addDefaultSTLTypes() {
        return stlTypeRepository.saveAll(
                Arrays.asList(
                        new STLType("Foundation"),
                        new STLType("CRM"),
                        new STLType("Talent")
                )
        );
    }
}

package io.electrica.stl.service;

import com.github.dozermapper.core.Mapper;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.stl.model.STL;
import io.electrica.stl.repository.STLRepository;
import io.electrica.stl.rest.dto.CreateSTLDto;
import io.electrica.stl.rest.dto.ReadSTLDto;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class STLService extends AbstractService<STL> {

    private final Mapper mapper;

    private final STLRepository stlRepository;

    private final ERNService ernService;

    public STLService(Mapper mapper, STLRepository stlRepository, ERNService ernService) {
        this.mapper = mapper;
        this.stlRepository = stlRepository;
        this.ernService = ernService;
    }

    public List<STL> findAll() {
        return stlRepository.findAll();
    }

    @Override
    protected STL executeCreate(STL model) {
        final String ern = ernService.assignERN(model.getName());
        model.setErn(ern);

        return stlRepository.save(model);
    }

    @Override
    protected void executeUpdate(STL merged, STL update) {
        throw new NotImplementedException("");
    }

    @Override
    protected JpaRepository<STL, Long> getRepository() {
        return stlRepository;
    }


    @Override
    protected Collection<String> getContainerValidators() {
        return Collections.emptyList();
    }

    @Override
    protected Collection<EntityValidator<STL>> getValidators() {
        return Collections.emptyList();
    }
}

package io.electrica.stl.service;

import io.electrica.common.jpa.service.AbstractService;
import io.electrica.common.jpa.service.validation.ContainerEntityValidator;
import io.electrica.common.jpa.service.validation.EntityValidator;
import io.electrica.stl.model.STL;
import io.electrica.stl.repository.STLRepository;
import io.electrica.common.helper.ERNUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class STLService extends AbstractService<STL> {

    private final STLRepository stlRepository;

    public STLService(STLRepository stlRepository) {
        this.stlRepository = stlRepository;
    }

    public List<STL> findAll() {
        return stlRepository.findAllNonArchived();
    }

    @Override
    protected STL executeCreate(STL model) {
        final String ern = ERNUtils.createERN(
                model.getName(),
                model.getResourceOpt(),
                model.getVersion()
        );
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
        return Arrays.asList(
                ContainerEntityValidator.TRIMMED_STRINGS,
                ContainerEntityValidator.AVOID_EMTPY_STRINGS
        );
    }

    @Override
    protected Collection<EntityValidator<STL>> getValidators() {
        return Collections.emptyList();
    }
}

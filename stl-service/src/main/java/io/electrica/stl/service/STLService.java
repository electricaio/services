package io.electrica.stl.service;

import io.electrica.common.helper.ERNUtils;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.STL;
import io.electrica.stl.model.STLType;
import io.electrica.stl.repository.STLRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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

        final STLType type = getReference(
                STLType.class, model.getType().getId()
        );
        model.setType(type);

        final AuthorizationType authorizationType = getReference(
                AuthorizationType.class, model.getAuthorizationType().getId()
        );
        model.setAuthorizationType(authorizationType);

        return stlRepository.save(model);
    }

    @Override
    protected void executeUpdate(STL merged, STL update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<STL, Long> getRepository() {
        return stlRepository;
    }

}

package io.electrica.stl.service;

import io.electrica.common.helper.ERNUtils;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.stl.model.AuthorizationType;
import io.electrica.stl.model.Connector;
import io.electrica.stl.model.ConnectorType;
import io.electrica.stl.repository.ConnectorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConnectorService extends AbstractService<Connector> {

    private final ConnectorRepository connectorRepository;

    public ConnectorService(ConnectorRepository connectorRepository) {
        this.connectorRepository = connectorRepository;
    }

    public List<Connector> findAll() {
        return connectorRepository.findAllNonArchived();
    }

    @Override
    protected Connector executeCreate(Connector model) {
        final String ern = ERNUtils.createERN(
                model.getName(),
                model.getResourceOpt(),
                model.getVersion()
        );
        model.setErn(ern);

        final ConnectorType type = getReference(
                ConnectorType.class, model.getType().getId()
        );
        model.setType(type);

        final AuthorizationType authorizationType = getReference(
                AuthorizationType.class, model.getAuthorizationType().getId()
        );
        model.setAuthorizationType(authorizationType);

        return connectorRepository.save(model);
    }

    @Override
    protected void executeUpdate(Connector merged, Connector update) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected JpaRepository<Connector, Long> getRepository() {
        return connectorRepository;
    }

}

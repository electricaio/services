package io.electrica.connector.hub.service;

import io.electrica.common.helper.ERNUtils;
import io.electrica.common.jpa.service.AbstractService;
import io.electrica.connector.hub.model.Connector;
import io.electrica.connector.hub.model.ConnectorType;
import io.electrica.connector.hub.repository.ConnectorRepository;
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
                model.getNamespace(),
                model.getResource(),
                model.getVersion()
        );
        model.setErn(ern);

        final ConnectorType type = getReference(ConnectorType.class, model.getType().getId());
        model.setType(type);

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

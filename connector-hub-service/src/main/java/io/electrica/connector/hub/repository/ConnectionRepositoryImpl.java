package io.electrica.connector.hub.repository;

import io.electrica.connector.hub.model.Connection;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;

public class ConnectionRepositoryImpl implements ConnectionRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Connection> findAllWithSearch(Long accessKeyId, String connectionName) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Connection> query = criteriaBuilder.createQuery(Connection.class);
        final Root<Connection> goodRoot = query.from(Connection.class);

        Predicate predicate = constructSearchCondition(accessKeyId, connectionName, criteriaBuilder, goodRoot);
        Order order = criteriaBuilder.asc(goodRoot.get("id"));

        final CriteriaQuery<Connection> criteriaQuery = query.where(predicate).orderBy(order);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private Predicate constructSearchCondition(Long accessKeyId, String connectionName, CriteriaBuilder criteriaBuilder,
                                               Root<Connection> goodRoot) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (accessKeyId != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(goodRoot.get("accessKeyId"), accessKeyId));
        }

        if (connectionName != null && !connectionName.isEmpty()) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(goodRoot.get("name"), connectionName));
        }
        return predicate;
    }
}

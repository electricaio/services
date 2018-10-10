package io.electrica.common.jpa.model;

public interface AuditedEntity {

    Long getRevisionVersion();

    void setRevisionVersion(Long version);

}

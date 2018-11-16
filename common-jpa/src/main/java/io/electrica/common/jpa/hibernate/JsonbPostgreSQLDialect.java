package io.electrica.common.jpa.hibernate;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class JsonbPostgreSQLDialect extends PostgreSQL94Dialect {

    public JsonbPostgreSQLDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, JsonObjectUserType.TYPE);
    }
}

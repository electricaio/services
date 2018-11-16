package io.electrica.common.hibernate;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

public class JSONBPostgreSQLDialect extends PostgreSQL94Dialect {

    public JSONBPostgreSQLDialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}

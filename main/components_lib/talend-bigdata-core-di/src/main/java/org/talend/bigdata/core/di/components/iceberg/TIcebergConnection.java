package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;

import java.sql.Connection;
import java.sql.SQLException;

@Value.Immutable
public abstract class TIcebergConnection {
    abstract String jdbcUrl();
    abstract String user();
    abstract String password();
    public Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(jdbcUrl(), user(), password());
    }
}
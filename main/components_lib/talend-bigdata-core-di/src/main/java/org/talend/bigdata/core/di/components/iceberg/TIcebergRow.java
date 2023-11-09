package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

@Value.Immutable
public abstract  class TIcebergRow {
    abstract Connection connection();

    abstract String sqlQuery();

    private static final Logger log = Logger.getLogger(TIcebergRow.class.getName());
    public void execute() throws SQLException {
        try (Statement statement = connection().createStatement()) {
            log.fine(sqlQuery());
            statement.executeUpdate(sqlQuery());
        }    }

}

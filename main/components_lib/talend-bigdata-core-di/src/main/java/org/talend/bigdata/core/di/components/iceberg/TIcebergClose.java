package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;

import java.sql.Connection;
import java.sql.SQLException;

@Value.Immutable
public abstract class TIcebergClose {

    abstract Connection connection();

    public void close() throws SQLException {
        connection().close();
    }
}

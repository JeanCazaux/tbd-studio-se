package org.talend.bigdata.core.di.components.iceberg;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TIcebergRowTest {

    @Test
    public void testExecute() throws SQLException {

        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);

        Mockito.when(connection.createStatement()).thenReturn(statement);

        ImmutableTIcebergRow tIcebergRow = ImmutableTIcebergRow.builder()
                .connection(connection)
                .sqlQuery("ALTER TABLE mytable ADD COLUMNS (newcol String)")
                .build();

        tIcebergRow.execute();

        Mockito.verify(statement).executeUpdate("ALTER TABLE mytable ADD COLUMNS (newcol String)");
    }
}

package org.talend.bigdata.core.di.components.iceberg;

import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TIcebergTableTest {

    @Test
    public void testCreate() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);

        Mockito.when(connection.createStatement()).thenReturn(statement);

        ImmutableTIcebergTable table = ImmutableTIcebergTable.builder()
                .table("mytable")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("my_int")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("my_date")
                        .type("Date").build())
                .format(TIcebergTable.Format.PARQUET)
                .build();

        table.create();
        Mockito.verify(statement).executeUpdate("CREATE  TABLE  mytable  (my_int Integer,my_date timestamp)  STORED BY 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler' STORED AS PARQUET   ");
    }

    @Test
    public void testDrop() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);

        Mockito.when(connection.createStatement()).thenReturn(statement);

        ImmutableTIcebergTable table = ImmutableTIcebergTable.builder()
                .table("mytable")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("mycolumn")
                        .type("Integer").build())
                .build();

        table.drop();
        Mockito.verify(statement).executeUpdate("DROP TABLE  mytable ");
    }

    @Test
    public void testTruncate() throws SQLException {
        Connection connection = Mockito.mock(Connection.class);
        Statement statement = Mockito.mock(Statement.class);

        Mockito.when(connection.createStatement()).thenReturn(statement);

        ImmutableTIcebergTable table = ImmutableTIcebergTable.builder()
                .table("mytable")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("mycolumn")
                        .type("Integer").build())
                .build();

        table.truncate();
        Mockito.verify(statement).executeUpdate("TRUNCATE TABLE mytable");
    }

}

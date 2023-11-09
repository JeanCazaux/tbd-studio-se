package org.talend.bigdata.core.di.components.iceberg;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TIcebergIT {

    private final String JDBC_URL=System.getProperty("JDBC_URL");
    private final String USER=System.getProperty("USER");
    private final String PASSWORD=System.getProperty("PASSWORD");

    private java.sql.Connection connection;

    private static final Logger log = Logger.getLogger( "icebergIT" );

    @Before
    public void testGetConnection() throws SQLException {

        log.fine("JDBC_URL : " + JDBC_URL);
        log.fine("USER : " + USER);

        ImmutableTIcebergConnection tIcebergConnection = ImmutableTIcebergConnection.builder()
                .jdbcUrl(JDBC_URL).user(USER).password(PASSWORD).build();
        connection = tIcebergConnection.getConnection();
        Assert.assertNotNull(connection);
    }

    @Test
    public void testWrite() throws Exception {
        ImmutableTIcebergOutput tIcebergOutput = ImmutableTIcebergOutput.builder()
                .connection(connection).table("iceberg_nonPartition_1")
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Byte")
                        .type("Byte").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Date")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_TimeStamp")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Double")
                        .type("Double").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Float")
                        .type("Float").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_BigDecimal")
                        .type("BigDecimal").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Integer")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Long")
                        .type("Long").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Short")
                        .type("Short").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_String")
                        .type("String").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Char")
                        .type("Character").build())
                .build();
        ImmutableRecord row = ImmutableRecord.builder()
                .putValues("t_Boolean", Boolean.TRUE)
                .putValues("t_Byte", new Byte("2"))
                .putValues("t_Date", new Date())
                .putValues("t_TimeStamp", new Timestamp(new Date().getTime()))
                .putValues("t_Double", 5.0)
                .putValues("t_Float", 6F)
                .putValues("t_BigDecimal", new BigDecimal(7))
                .putValues("t_Integer", 8)
                .putValues("t_Long", 9L)
                .putValues("t_Short", (short) 10)
                .putValues("t_String", "From IT")
                .putValues("t_Char", "a".charAt(0)).build();

        tIcebergOutput.write(row);
        connection.close();
    }

    @Test
    public void testRead() throws Exception {
        ImmutableTIcebergInput tIcebergInput = ImmutableTIcebergInput.builder()
                .connection(connection)
                .sqlQuery("select t_Boolean,t_Byte,t_Date,t_TimeStamp,t_Double,t_Float,t_BigDecimal,t_Integer,t_Long,t_Short,t_String,t_Char from iceberg_nonPartition_1")
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Byte")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Date")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_TimeStamp")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Double")
                        .type("Double").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Float")
                        .type("Float").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_BigDecimal")
                        .type("BigDecimal").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Integer")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Long")
                        .type("Long").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Short")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_String")
                        .type("String").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Char")
                        .type("Character").build())
                .build();
        Collection<ImmutableRecord> records = tIcebergInput.read();
        Assert.assertTrue(records.size()>0);
        records.forEach(System.out::println);
        for (ImmutableRecord record : records) {
            Boolean tBoolean = (Boolean) record.get("t_Boolean");
            Character tChar = (Character) record.get("t_Char");
            Integer tByte = (Integer) record.get("t_Byte");
            Date tDate = (Date) record.get("t_Date");
            Date tTimeStamp = (Date) record.get("t_TimeStamp");
            Double tDouble = (Double) record.get("t_Double");
            Float tFloat = (Float) record.get("t_Float");
            BigDecimal tBigDecimal = (BigDecimal) record.get("t_BigDecimal");
            Integer tInteger = (Integer) record.get("t_Integer");
            Long tLong = (Long) record.get("t_Long");
            Integer tShort = (Integer) record.get("t_Short");
            String tString = (String) record.get("t_String");
        }
    }

    @Test
    public void testRow() throws SQLException {
        ImmutableTIcebergRow tIcebergRowCreate = ImmutableTIcebergRow.builder()
                .connection(connection)
                .sqlQuery("CREATE TABLE testrow (mycol string)")
                .build();
        tIcebergRowCreate.execute();

        ImmutableTIcebergRow tIcebergRowDrop = ImmutableTIcebergRow.builder()
                .connection(connection)
                .sqlQuery("DROP TABLE testrow")
                .build();
        tIcebergRowDrop.execute();
    }

    private void dropTable(String name) throws SQLException {
        ImmutableTIcebergTable table = ImmutableTIcebergTable.builder()
                .table("mytable")
                .connection(connection)
                .build();
        table.drop();
    }

    @Test
    public void testCreateTable() throws SQLException {
        ImmutableTIcebergTable table = ImmutableTIcebergTable.builder()
                .table("mytable")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Byte")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Date")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_TimeStamp")
                        .type("Date").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Double")
                        .type("Double").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Float")
                        .type("Float").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_BigDecimal")
                        .type("BigDecimal").scale(9).precision(7).build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Integer")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Long")
                        .type("Long").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Short")
                        .type("Integer").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_String")
                        .type("String").build())
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Char")
                        .type("Character").build())
                .build();
        table.drop(true);
        table.create();

        ImmutableTIcebergTable tableLike = ImmutableTIcebergTable.builder()
                .table("mytablelike")
                .connection(connection)
                .createLikeSourceTable("mytable")
                .format(TIcebergTable.Format.ORC)
                .build();
        tableLike.drop(true);
        tableLike.create();

        ImmutableTIcebergTable tableAsSelect = ImmutableTIcebergTable.builder()
                .table("mytableselect")
                .connection(connection)
                .createAsSelectQuery("SELECT * FROM mytablelike")
                .external(true)
                .build();
        tableAsSelect.drop(true);
        tableAsSelect.create();

        ImmutableTIcebergTable tableExternal = ImmutableTIcebergTable.builder()
                .table("mytableexternal")
                .external(true)
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .build();
        tableExternal.drop(true);
        tableExternal.create();

        ImmutableTIcebergTable tablePartitions = ImmutableTIcebergTable.builder()
                .table("mytablepartitions")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .addPartitions(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Integer")
                        .type("Integer").build())
                .addPartitions(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Byte")
                        .type("Integer").build())
                .build();
        tablePartitions.drop(true);
        tablePartitions.create();

        ImmutableTIcebergTable tableProperties = ImmutableTIcebergTable.builder()
                .table("mytableproperties")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .putTableProperties("mykey1", "myvalue1")
                .putTableProperties("mykey2", "myvalue2")
                .build();
        tableProperties.drop(true);
        tableProperties.create();

        ImmutableTIcebergTable tableFormat = ImmutableTIcebergTable.builder()
                .table("mytableformat")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .format(TIcebergTable.Format.AVRO)
                .build();
        tableFormat.drop(true);
        tableFormat.create();

        ImmutableTIcebergTable tableLocation = ImmutableTIcebergTable.builder()
                .table("mytablelocation")
                .connection(connection)
                .addFields(org.talend.bigdata.core.di.components.ImmutableField.builder().name("t_Boolean")
                        .type("Boolean").build())
                .location("s3a://lbourgeois-cde-bucket/location_test")
                .purge(true)
                .build();
        tableLocation.truncate();
        tableLocation.drop(true);
        tableLocation.create();
    }

    @After
    public void testClose() throws SQLException {
        ImmutableTIcebergClose tIcebergClose = ImmutableTIcebergClose.builder().connection(connection).build();
        tIcebergClose.close();
    }

}

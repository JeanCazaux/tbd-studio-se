package org.talend.bigdata.core.di.components.iceberg;

import org.junit.*;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TIcebergImpalaIT {


    private static final String JDBC_URL=System.getProperty("JDBC_URL");
    private static final String USER=System.getProperty("USER");
    private static final String PASSWORD=System.getProperty("PASSWORD");
    private static final Logger log = Logger.getLogger( "icebergIT" );

    private Connection getConnection() throws SQLException {

        log.log(Level.INFO, "JDBC_URL : " + JDBC_URL);
        log.log(Level.INFO, "USER : " + USER);

        ImmutableTIcebergConnection tIcebergConnection = ImmutableTIcebergConnection.builder()
                .jdbcUrl(JDBC_URL).user(USER).password(PASSWORD).build();
        Connection connection = tIcebergConnection.getConnection();
        Assert.assertNotNull(connection);
        return connection;
    }

    @Test
    public void testReadWrite() throws Exception {
        Connection connection = getConnection();
        int nbRecordsBefore = read(connection);
        write(connection);
        int nbRecordsAfter = read(connection);
        Assert.assertEquals(nbRecordsBefore + 3 , nbRecordsAfter);
        close(connection);
    }

    private void write(Connection connection) throws Exception {
        log.info("Writing");
        ImmutableTIcebergOutput tIcebergOutput = ImmutableTIcebergOutput.builder()
                .connection(connection).table("iceberg_nonPartition_1")
                .useBatch(true)
                .batchSize(5)
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
        tIcebergOutput.write(row);
        tIcebergOutput.write(row);
        tIcebergOutput.flush();
    }

    private int read(Connection connection) throws Exception {
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
        return records.size();
    }

    private void close(Connection connection) throws SQLException {
        log.info("testClose()");
        ImmutableTIcebergClose tIcebergClose = ImmutableTIcebergClose.builder().connection(connection).build();
        tIcebergClose.close();
    }

}

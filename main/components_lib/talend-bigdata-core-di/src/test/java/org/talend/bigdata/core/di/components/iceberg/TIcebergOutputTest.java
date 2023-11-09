package org.talend.bigdata.core.di.components.iceberg;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

public class TIcebergOutputTest {

    @Test
    public void testWrite() throws Exception {

        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);

        Mockito.when(connection.prepareStatement(ArgumentMatchers.anyString())).thenReturn(statement);

        ImmutableTIcebergOutput tIcebergOutput = ImmutableTIcebergOutput.builder()
                .connection(connection)
                .table("mytable")
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

        Date d = new Date();
        Timestamp t = new Timestamp(d.getTime());

        ImmutableRecord record = ImmutableRecord.builder()
                .putValues("t_Boolean", Boolean.TRUE)
                .putValues("t_Byte", new Byte("2"))
                .putValues("t_Date", d)
                .putValues("t_TimeStamp", t)
                .putValues("t_Double", 5.0)
                .putValues("t_Float", 6F)
                .putValues("t_BigDecimal", new BigDecimal(7))
                .putValues("t_Integer", 8)
                .putValues("t_Long", 9L)
                .putValues("t_Short", (short) 10)
                .putValues("t_String", "Unit Test")
                .putValues("t_Char", "a".charAt(0)).build();

        tIcebergOutput.write(record);
        Mockito.verify(statement).setBoolean(1,Boolean.TRUE);
        Mockito.verify(statement).setByte(2, new Byte("2"));
        Mockito.verify(statement).setTimestamp(3, new Timestamp(d.getTime()));
        Mockito.verify(statement).setTimestamp(4, t);
        Mockito.verify(statement).setDouble(5, 5.0);
        Mockito.verify(statement).setFloat(6, 6F);
        Mockito.verify(statement).setBigDecimal(7, new BigDecimal(7));
        Mockito.verify(statement).setInt(8, 8);
        Mockito.verify(statement).setLong(9, 9L);
        Mockito.verify(statement).setShort(10, (short) 10);
        Mockito.verify(statement).setString(11, "Unit Test");
        Mockito.verify(statement).setString(12, "a");
    }
}

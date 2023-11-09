package org.talend.bigdata.core.di.components.iceberg;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class TIcebergInputTest {

    @Test
    public void testRead() throws Exception {

        String sqlQuery = "SELECT col FROM mytable";

        Connection connection = Mockito.mock(Connection.class);
        PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(connection.prepareStatement(sqlQuery)).thenReturn(statement);
        Mockito.when(statement.executeQuery()).thenReturn(resultSet);
        Mockito.when(resultSet.next()).thenReturn(true).thenReturn(false);

        ImmutableTIcebergInput tIcebergInput = ImmutableTIcebergInput.builder()
                .connection(connection)
                .sqlQuery(sqlQuery)
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
        Mockito.when(resultSet.getBoolean("t_Boolean")).thenReturn(true);
        Mockito.when(resultSet.getByte("t_Byte")).thenReturn(new Byte("2"));
        Mockito.when(resultSet.getTimestamp("t_Date")).thenReturn(t);
        Mockito.when(resultSet.getTimestamp("t_TimeStamp")).thenReturn(t);
        Mockito.when(resultSet.getDouble("t_Double")).thenReturn(5.0);
        Mockito.when(resultSet.getFloat("t_Float")).thenReturn(6F);
        Mockito.when(resultSet.getBigDecimal("t_BigDecimal")).thenReturn(new BigDecimal(7));
        Mockito.when(resultSet.getInt("t_Integer")).thenReturn(8);
        Mockito.when(resultSet.getLong("t_Long")).thenReturn(9L);
        Mockito.when(resultSet.getShort("t_Short")).thenReturn((short) 10);
        Mockito.when(resultSet.getString("t_String")).thenReturn("Unit test");
        Mockito.when(resultSet.getString("t_Char")).thenReturn("U");

        Collection<ImmutableRecord> records = tIcebergInput.read();
        Assert.assertEquals(1, records.size());
        records.forEach(System.out::println);
        ImmutableRecord record = ((ArrayList<ImmutableRecord>) records).get(0);
        Assert.assertEquals(true, record.get("t_Boolean"));
        Assert.assertEquals(new Byte("2"), record.get("t_Byte"));
        Assert.assertEquals(t, record.get("t_Date"));
        Assert.assertEquals(t, record.get("t_TimeStamp"));
        Assert.assertEquals(5.0, record.get("t_Double"));
        Assert.assertEquals(6F, record.get("t_Float"));
        Assert.assertEquals(new BigDecimal(7), record.get("t_BigDecimal"));
        Assert.assertEquals(8, record.get("t_Integer"));
        Assert.assertEquals(9L, record.get("t_Long"));
        Assert.assertEquals((short) 10, record.get("t_Short"));
        Assert.assertEquals("Unit test", record.get("t_String"));
        Assert.assertEquals("U".charAt(0), record.get("t_Char"));
    }
}

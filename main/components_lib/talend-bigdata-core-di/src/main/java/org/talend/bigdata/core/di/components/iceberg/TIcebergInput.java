package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;
import org.talend.bigdata.core.di.components.Component;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

@Value.Immutable
public abstract class TIcebergInput implements Component.WithSchema, Component.Reader {
    public abstract Connection connection();
    public abstract String sqlQuery();

    @Override
    public Collection<ImmutableRecord> read() throws Exception {
        Collection<ImmutableRecord> result = new ArrayList<>();
        try (
                PreparedStatement st = connection().prepareStatement(sqlQuery());
                ResultSet rs = st.executeQuery();
        ){
            while (rs.next()) {
                ImmutableRecord.Builder recordBuilder = ImmutableRecord.builder();
                for (Component.Field f :fields()) {
                    recordBuilder.putValues(f.name(), resultSetToObject(rs,f.name(),f.type()));
                }
                result.add(recordBuilder.build());
            }
        }
        return result;
    }

    private Object resultSetToObject(ResultSet resultSet, String column, String type) throws SQLException {
        switch (type) {
            case "Boolean":
                return resultSet.getBoolean(column);
            case "Integer":
                return resultSet.getInt(column);
            case "Byte":
                return resultSet.getByte(column);
            case "Date":
                return resultSet.getTimestamp(column);
            case "Long":
                return resultSet.getLong(column);
            case "Short":
                return resultSet.getShort(column);
            case "Double":
                return resultSet.getDouble(column);
            case "Float":
                return resultSet.getFloat(column);
            case "BigDecimal":
                return resultSet.getBigDecimal(column);
            case "Character":
                return resultSet.getString(column)==null?null:resultSet.getString(column).charAt(0);
            default:
                    return resultSet.getString(column);
        }
    }
}

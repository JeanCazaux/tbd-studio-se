package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;
import org.talend.bigdata.core.di.components.Component;
import org.talend.bigdata.core.di.components.ImmutableRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class TIcebergOutput implements Component.WithSchema, Component.Writer {

    private final Collection<ImmutableRecord> batchedRecords = new ArrayList<>();

    public abstract Connection connection();

    public abstract  String table();
    @Value.Default
    public boolean useBatch() {
        return false;
    }

    @Value.Default
    public int batchSize() {
        return 10;
    }

    @Override
    public void write(ImmutableRecord record) throws Exception {
        if (!useBatch()) {
            try (PreparedStatement statement = connection().prepareStatement(getSqlInsertStatement())) {
                setStatement(statement, record);
                statement.executeUpdate();
            }
        } else {
            addToBatch(record);
            if (batchedRecords.size() == batchSize()) {
                writeBatch();
            }
        }
    }

    public void flush() throws SQLException {
        if (useBatch() && !batchedRecords.isEmpty()) {
            writeBatch();
        }
    }

    private void addToBatch(ImmutableRecord record) {
        batchedRecords.add(record);
    }

    private void writeBatch() throws SQLException {
        write(batchedRecords);
        batchedRecords.clear();
    }

    /**
     * Write records in a batch mode
     * @param records
     * @throws SQLException
     */
    private void write(Collection<ImmutableRecord> records) throws SQLException {
        try (PreparedStatement statement = connection().prepareStatement(getSqlInsertStatement())) {
            for (ImmutableRecord record:records) {
                setStatement(statement, record);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private String getSqlInsertStatement() {
        return String.format("INSERT INTO %1$s (%2$s) VALUES (%3$s)", table(), getFields(), fields().stream().map(f -> "?").collect(Collectors.joining(",")));
    }

    private void setStatement(PreparedStatement statement, ImmutableRecord record) throws SQLException {
        int index = 0;
        for (Component.Field f : fields()) {
            index++;
            setStatement(statement, index, f.type(), record.get(f.name()));
        }
    }

    private void setStatement(PreparedStatement statement, int index, String type, Object value) throws SQLException {
        if (value == null) return;
        switch (type) {
            case "Boolean":
                statement.setBoolean(index, (Boolean) value); break;
            case "Integer":
                statement.setInt(index, (Integer) value); break;
            case "Byte":
                statement.setByte(index, (Byte) value); break;
            case "Date":
                if (value instanceof Timestamp) {
                    statement.setTimestamp(index, (Timestamp) value);
                } else {
                    statement.setTimestamp(index, new Timestamp(((Date) value).getTime()));
                }
                break;
            case "Long":
                statement.setLong(index, (Long) value); break;
            case "Short":
                statement.setShort(index, (Short) value); break;
            case "Double":
                statement.setDouble(index, (Double) value); break;
            case "Float":
                statement.setFloat(index, (Float) value); break;
            case "BigDecimal":
                statement.setBigDecimal(index, (BigDecimal) value); break;
            case "Character":
                statement.setString(index, Character.toString((Character) value)); break;
            default: statement.setString(index, (String) value); break;
        }
    }
}

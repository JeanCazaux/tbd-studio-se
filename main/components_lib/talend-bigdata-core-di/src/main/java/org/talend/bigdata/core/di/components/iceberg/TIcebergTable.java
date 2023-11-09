package org.talend.bigdata.core.di.components.iceberg;

import org.immutables.value.Value;
import org.talend.bigdata.core.di.components.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Value.Immutable
public abstract class TIcebergTable implements Component.WithSchema {

    public enum Format{AVRO, ORC, PARQUET}

    private static final Logger log = Logger.getLogger(TIcebergTable.class.getName());

    public abstract Connection connection();

    public abstract Optional<String> createAsSelectQuery();

    public abstract Optional<String> createLikeSourceTable();

    @Value.Default
    public boolean external() {
        return false;
    };

    public abstract Optional<Format> format();

    public abstract Optional<String> location();

    @Value.Default
    public List<Component.Field> partitions() {
        return Collections.emptyList();
    };

    @Value.Default
    public boolean purge() {
        return false;
    }

    public abstract String table();

    @Value.Default
    public Map<String, String> tableProperties() {
        return Collections.emptyMap();
    }

    public void create() throws SQLException {
        create(false);
    }

    public void create(Boolean ifNotExists)  throws SQLException{
        try (Statement statement = connection().createStatement()) {
            String sql = getCreateTableStatement(ifNotExists);
            log.fine(sql);
            statement.executeUpdate(sql);
        }
    }

    public void drop() throws SQLException {
        drop(false);
    }
    public void drop(boolean ifExists) throws SQLException {
        try (Statement statement = connection().createStatement()) {
            String sql = getDropTableStatement(ifExists);
            log.fine(sql);
            statement.executeUpdate(sql);
        }
    }

    public void truncate()  throws SQLException{
        try (Statement statement = connection().createStatement()) {
            String sql = getTruncateTableStatement();
            log.fine(sql);
            statement.executeUpdate(sql);
        }
    }

    private String getAsSelectSql(){
        return createAsSelectQuery().isPresent()?String.format("AS %1$s", createAsSelectQuery().get()):"";
    }

    private String getColumnsSql() {
        return createAsSelectQuery().isPresent()|| createLikeSourceTable().isPresent()?"":String.format("(%1$s)",fields().stream().map(f -> f.name() + " " + IcebergField.sqlType(f)).collect(Collectors.joining(",")));
    }

    private String getCreateTableStatement(boolean ifNotExists){
        return(String.format("CREATE %1$s TABLE %2$s %3$s %4$s %5$s %6$s STORED BY 'org.apache.iceberg.mr.hive.HiveIcebergStorageHandler' %7$s %8$s %9$s %10$s",
                getExternalSql(), // 1
                getCreateConditionSql(ifNotExists), // 2
                table(), // 3
                getLikeSql(), // 4
                getColumnsSql(), // 5
                getPartitionsSql(), // 6
                getStorageSql(), // 7
                getLocationSql(), // 8
                getAsSelectSql(), // 9
                getTablePropertiesSql())); // 10
    }

    private String getCreateConditionSql(boolean ifNotExists) {
        return ifNotExists?"IF NOT EXISTS":"";
    }

    private String getLikeSql() {
        return createLikeSourceTable().isPresent()?String.format("LIKE %1$s", createLikeSourceTable().get()):"";
    }

    private String getLocationSql() {
        return location().isPresent()?String.format("LOCATION '%1$s'", location().get()):"";
    }
    private String getPartitionsSql() {
        return partitions().isEmpty()?"":String.format(" PARTITIONED BY (%1$s) ", partitions().stream().map(f -> f.name() + " " + IcebergField.sqlType(f)).collect(Collectors.joining(",")));
    }

    private String getStorageSql(){
        return format().isPresent()?String.format("STORED AS %1$s", format().get()):"";
    }

    private String getExternalSql(){
        return external()?"EXTERNAL":"";
    }

    private String getDropTableStatement(boolean ifExists){
        String dropCondition = ifExists?" IF EXISTS ":"";
        return String.format("DROP TABLE %1$s %2$s %3$s", dropCondition, table(), purge()?"PURGE":"");
    }

    private String getTablePropertiesSql(){
        return tableProperties().isEmpty()?"":String.format("TBLPROPERTIES (%1$s)", tableProperties().entrySet().stream().map(e -> String.format("'%1$s'='%2$s'", e.getKey(), e.getValue())).collect(Collectors.joining(",")));
    }

    private String getTruncateTableStatement() {
        return String.format("TRUNCATE TABLE %1$s", table());
    }

}

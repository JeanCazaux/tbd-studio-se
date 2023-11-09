package org.talend.bigdata.core.di.components.hbase;

import org.apache.hadoop.conf.Configuration;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class THbaseConfigurationTest {

    @Test
    public void checkConfiguration() {
        ImmutableTHbaseConnection immutableTHbaseConfiguration = ImmutableTHbaseConnection.builder()
                .globalConfiguration(new Configuration())
                .putHBaseConfigurationOptions("hbase.zookeeper.quorum","localhost")
                .putHBaseConfigurationOptions("hbase.zookeeper.property.clientPort","2108")
                .isUseExistingConnection(false)
                .build();

        immutableTHbaseConfiguration.createConfiguration();
        Assertions.assertThat(immutableTHbaseConfiguration.getConfiguration()).isNotEmpty();

        Optional<Configuration> configuration = Optional.ofNullable(new Configuration());
        immutableTHbaseConfiguration = ImmutableTHbaseConnection.builder()
                .globalConfiguration(configuration)
                .putHBaseConfigurationOptions("hbase.zookeeper.quorum","localhost")
                .putHBaseConfigurationOptions("hbase.zookeeper.property.clientPort","2108")
                .isUseExistingConnection(true)
                .build();

        immutableTHbaseConfiguration.createConfiguration();
        Assertions.assertThat(immutableTHbaseConfiguration.getConfiguration()).isNotEmpty();

    }
    @Test
    public void configurationIsNull() {
        Optional<Configuration> configuration = Optional.ofNullable(null);
        ImmutableTHbaseConnection immutableTHbaseConfiguration = ImmutableTHbaseConnection.builder()
                .globalConfiguration(configuration)
                .putHBaseConfigurationOptions("hbase.zookeeper.quorum","localhost")
                .putHBaseConfigurationOptions("hbase.zookeeper.property.clientPort","2108")
                .isUseExistingConnection(true)
                .build();

        Throwable exception = assertThrows(RuntimeException.class, immutableTHbaseConfiguration::createConfiguration);
        assertEquals("Configuration for HBase connection is null", exception.getMessage());
    }

}
package org.talend.bigdata.core.di.components;

import org.immutables.value.Value;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Component {

    interface WithSchema {
        List<Field> fields();

        default String getFields() {
            return fields().stream().map(Component.Field::name).collect(Collectors.joining(","));
        }
    }

    @Value.Immutable
    interface Field {
        String name();

        String type();

        Optional<Integer> scale();
        Optional<Integer> precision();

        @Value.Default
        default String pattern() {
            return "";
        }
    }

    interface Writer {
        void write(ImmutableRecord record) throws Exception;
    }

    interface Reader {
        Collection<ImmutableRecord> read() throws Exception;
    }
}
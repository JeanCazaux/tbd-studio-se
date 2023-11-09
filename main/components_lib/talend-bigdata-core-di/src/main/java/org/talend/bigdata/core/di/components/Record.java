package org.talend.bigdata.core.di.components;

import org.immutables.fixture.modifiable.AllowNulls;
import org.immutables.value.Value;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
public interface Record {

    @AllowNulls Map<String, Object> values();

    default Object get(String column){
        return values().get(column);
    }
}

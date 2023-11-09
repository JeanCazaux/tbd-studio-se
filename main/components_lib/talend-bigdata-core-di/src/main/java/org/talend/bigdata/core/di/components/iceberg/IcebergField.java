package org.talend.bigdata.core.di.components.iceberg;

import org.talend.bigdata.core.di.components.Component;

public class IcebergField {

    public static String sqlType(Component.Field f) {
        switch (f.type()) {
            case "BigDecimal" : return String.format("decimal%1$s", getScalePrecisionSql(f));
            case "Long" : return "bigint";
            case "Character" : return "string";
            case "Date" : return "timestamp";
            default:return f.type();
        }
    }

    private static String getScalePrecisionSql(Component.Field f){
        return f.scale().isPresent()&&f.precision().isPresent()?String.format("(%1$s,%2$s)", f.scale().get(), f.precision().get()):"";
    }
}

// ============================================================================
//
// Copyright (C) 2006-2023 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.hadoop.distribution.constants.hdinsight;

import java.util.ArrayList;
import java.util.List;

public enum EHdiAuthType {

	SECRETKEY("Secret Key"),

	AAD("Azure Active Directory");

    private String displayName;

    EHdiAuthType(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name();
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public static List<String> getAllHdiAuthTypes() {
        return getAllHdiAuthTypes(true);
    }

    public static List<String> getAllHdiAuthTypes(boolean display) {
        List<String> types = new ArrayList<String>();
        EHdiAuthType[] values = values();
        for (EHdiAuthType authType : values) {
            if (display) {
            	types.add(authType.getDisplayName());
            } else {
            	types.add(authType.getName());
            }
        }
        return types;
    }

    public static EHdiAuthType getHdiAuthTypeByDisplayName(String name) {
        return getHdiAuthTypeByName(name, true);
    }

    public static EHdiAuthType getHdiAuthTypeByName(String type, boolean display) {
        if (type != null) {
            for (EHdiAuthType authType : values()) {
                if (display) {
                    if (type.equalsIgnoreCase(authType.getDisplayName())) {
                        return authType;
                    }
                } else {
                    if (type.equalsIgnoreCase(authType.getName())) {
                        return authType;
                    }
                }
            }
        }
        return null;
    }
}

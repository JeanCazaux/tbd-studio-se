// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.repository.nosql.db.common.mongodb;

/**
 * created by ycbai on Jul 4, 2014 Detailled comment
 *
 */
public interface IMongoConstants {

    public final static String DATABASE = "Database"; //$NON-NLS-1$

    public final static String COLLECTION = "Collection"; //$NON-NLS-1$

    public final static String REPLICA_HOST_KEY = "REPLICA_HOST"; //$NON-NLS-1$

    public final static String REPLICA_PORT_KEY = "REPLICA_PORT"; //$NON-NLS-1$

    public final static String DEFAULT_HOST = "localhost"; //$NON-NLS-1$

    public final static String DEFAULT_PORT = "27017"; //$NON-NLS-1$

    public final static String NEGOTIATE_MEC = "NEGOTIATE_MEC";
    
    public final static String PLAIN_MEC = "PLAIN_MEC";
    
    public final static String SCRAMSHA1_MEC = "SCRAMSHA1_MEC";
    
    public final static String SCRAMSHA256_MEC = "SCRAMSHA256_MEC";
    
    public final static String KERBEROS_MEC = "KERBEROS_MEC";
    
    public final static String DEFAULT_KRB_USER_PRINCIPAL = "mongouser@EXAMPLE.COM";
    
    public final static String DEFAULT_KRB_REALM = "EXAMPLE.COM";
    
    public final static String DEFAULT_KRB_KDC = "kdc.example.com";
    
}

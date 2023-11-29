package org.talend.bigdata.core.di.components.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class THbaseTableTest {

    @Test
    public void  checkOneFamilyParameter() throws IOException {
        List<Map<String, String>> familyParameters = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("FAMILY_NAME","f1");
        familyParameters.add(map);

        ImmutableTHbaseTable immutableTHbaseTableEmptyParam = ImmutableTHbaseTable
                .builder()
                .connection(ConnectionFactory.createConnection(new Configuration()))
                .isSpecifyNamespace(true)
                .namespaceName("ns")
                .tableName("table")
                .tableAction("NONE")
                .addAllFamilyParameters(familyParameters)
                .regionSplitKeys("")
                .createTableFunction(((tableName, admin, family, regionSplitKeysByteArr)->{
                    System.out.println("create table");
                }))
                .build();

        List<HColumnDescriptor> expectedFamilyParameters = immutableTHbaseTableEmptyParam.getColumnFamilies();
        Assert.assertEquals(expectedFamilyParameters.size(), 1);
        Assert.assertEquals(expectedFamilyParameters.get(0).getNameAsString(), "f1");
        Assert.assertNull(immutableTHbaseTableEmptyParam.convertRegionSplitKeys());
    }

    @Test
    public void checkAllFamilyParameters() throws IOException {
        List<Map<String, String>> familyParameters = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("FAMILY_NAME","f1");
        map.put("FAMILY_INMEMORY","true");
        map.put("FAMILY_BLOCKCACHEENABLED","true");
        map.put("FAMILY_BLOOMFILTERTYPE","NONE");
        map.put("FAMILY_BLOCKSIZE","11111");
        map.put("FAMILY_COMPACTIONCOMPRESSIONTYPE","GZ");
        map.put("FAMILY_COMPRESSIONTYPE","LZO");
        map.put("FAMILY_MAXVERSIONS","11111");
        map.put("FAMILY_SCOPE","1");
        map.put("FAMILY_TIMETOLIVE","11111");
        familyParameters.add(map);

        map = new HashMap<>();
        map.put("FAMILY_NAME","f2");
        map.put("FAMILY_INMEMORY","false");
        map.put("FAMILY_BLOCKCACHEENABLED","false");
        map.put("FAMILY_BLOOMFILTERTYPE","ROW");
        map.put("FAMILY_BLOCKSIZE","2222");
        map.put("FAMILY_COMPACTIONCOMPRESSIONTYPE","LZO");
        map.put("FAMILY_COMPRESSIONTYPE","GZ");
        map.put("FAMILY_MAXVERSIONS","222");
        map.put("FAMILY_SCOPE","0");
        map.put("FAMILY_TIMETOLIVE","22222");
        familyParameters.add(map);

        String str_keys = "key1, key2, key3";

        ImmutableTHbaseTable immutableTHbaseTable = ImmutableTHbaseTable
                .builder()
                .connection(ConnectionFactory.createConnection(new Configuration()))
                .isSpecifyNamespace(true)
                .namespaceName("ns")
                .tableName("table")
                .tableAction("NONE")
                .regionSplitKeys(str_keys)
                .addAllFamilyParameters(familyParameters)
                .createTableFunction(((tableName, admin, family, regionSplitKeysByteArr)->{
                    System.out.println("create table");
                }))
                .build();

        List<HColumnDescriptor> expectedFamilyParameters = immutableTHbaseTable.getColumnFamilies();
        Assert.assertEquals(expectedFamilyParameters.size(), 2);

        Assert.assertEquals(expectedFamilyParameters.get(0).getNameAsString(), "f1");
        Assert.assertTrue(expectedFamilyParameters.get(0).isInMemory());
        Assert.assertEquals(expectedFamilyParameters.get(0).getBloomFilterType().toString(), "NONE");
        Assert.assertEquals(expectedFamilyParameters.get(0).getBlocksize(),11111);
        Assert.assertEquals(expectedFamilyParameters.get(0).getCompactionCompressionType().toString(),"GZ");
        Assert.assertEquals(expectedFamilyParameters.get(0).getCompressionType().toString(), "LZO");
        Assert.assertEquals(expectedFamilyParameters.get(0).getMaxVersions(), 11111);
        Assert.assertEquals(expectedFamilyParameters.get(0).getScope(),1);
        Assert.assertEquals(expectedFamilyParameters.get(0).getTimeToLive(),11111);

        Assert.assertEquals(expectedFamilyParameters.get(1).getNameAsString(), "f2");
        Assert.assertFalse(expectedFamilyParameters.get(1).isInMemory());
        Assert.assertEquals(expectedFamilyParameters.get(1).getBloomFilterType().toString(), "ROW");
        Assert.assertEquals(expectedFamilyParameters.get(1).getBlocksize(),2222);
        Assert.assertEquals(expectedFamilyParameters.get(1).getCompactionCompressionType().toString(),"LZO");
        Assert.assertEquals(expectedFamilyParameters.get(1).getCompressionType().toString(), "GZ");
        Assert.assertEquals(expectedFamilyParameters.get(1).getMaxVersions(), 222);
        Assert.assertEquals(expectedFamilyParameters.get(1).getScope(),0);
        Assert.assertEquals(expectedFamilyParameters.get(1).getTimeToLive(),22222);

        String[] regSplitKeyArray = str_keys.split(",");
        byte[][] byteArr = new byte[regSplitKeyArray.length][];
        for (int i = 0; i < regSplitKeyArray.length; i++) {
            byte[] b = regSplitKeyArray[i].getBytes();
            byteArr[i] = b;
        }

        Assert.assertEquals(immutableTHbaseTable.convertRegionSplitKeys().length, 3);
        Assert.assertArrayEquals(immutableTHbaseTable.convertRegionSplitKeys(), byteArr);
    }

}

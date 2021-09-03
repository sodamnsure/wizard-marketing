package org.wizard.marketing.core.service.query;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/3 4:22 下午
 * @Desc:
 */
public class HbaseQueryServiceImpl implements QueryService {
    Connection hbaseConn;

    public HbaseQueryServiceImpl(Connection hbaseConn) {
        this.hbaseConn = hbaseConn;
    }

    public boolean queryProfileCondition(String deviceId, Map<String, String> profileConditions) throws IOException {
        Set<String> tags = profileConditions.keySet();

        Table table = hbaseConn.getTable(TableName.valueOf("user_profile"));
        Get get = new Get(deviceId.getBytes());
        for (String tag : tags) {
            get.addColumn("f".getBytes(), tag.getBytes());
        }

        Result result = table.get(get);

        for (String tag : tags) {
            byte[] value = result.getValue("f".getBytes(), tag.getBytes());
            System.out.println("查询到一个标签: " + tag + " = " + new String(value));
            if (!profileConditions.get(tag).equals(new String(value))) return false;
        }
        return true;
    }
}

package org.wizard.marketing.core.datagen;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/9 3:20 下午
 * @Desc:
 */
public class UserProfileDataGen {
    public static void main(String[] args) throws IOException {
        // 创建HBASE配置
        Configuration conf = new Configuration();
        conf.set("hbase.zookeeper.quorum", "feelings:2181");
        // 创建HBASE链接
        Connection conn = ConnectionFactory.createConnection(conf);
        // 创建链接后要拿到表
        Table table = conn.getTable(TableName.valueOf("user_profile"));

        for (int i = 1; i < 1000000; i++) {
            ArrayList<Put> puts = new ArrayList<>();
            // 攒满20条数据作为一批发送到HBASE
            for (int z = 0; z < 20; z++) {
                // 生成一个用户的画像标签数据
                String deviceId = StringUtils.leftPad(i + "", 6, "0");
                // 创建Put对象，输入RowKey
                Put put = new Put(Bytes.toBytes(deviceId));
                // 给这个人生成1千个KV
                for (int j = 1; j <= 10; j++) {
                    String key = "k" + j;
                    String value = "v" + RandomUtils.nextInt(1, 10);
                    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes(key), Bytes.toBytes(value));
                }
                // 将这条画像数据添加到list中
                puts.add(put);
            }
            table.put(puts);
            // 清空List
            puts.clear();
        }
        // 关闭链接
        conn.close();
    }
}

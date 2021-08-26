package org.wizard.marketing.core.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.wizard.marketing.core.common.constant.LoadConst;

import java.io.IOException;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/26 7:57 下午
 * @Desc: 客户端链接创建工具
 */
public class ConnectionUtils {
    static Config config = ConfigFactory.load();

    public Connection getHbaseConnection() throws IOException {
        // 创建HBASE配置
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", config.getString(LoadConst.HBASE_ZK_QUORUM));
        // 创建HBASE链接
        return ConnectionFactory.createConnection(conf);
    }

}

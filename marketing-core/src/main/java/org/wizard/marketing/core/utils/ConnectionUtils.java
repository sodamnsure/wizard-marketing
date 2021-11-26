package org.wizard.marketing.core.utils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.sql.DriverManager;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/26 7:57 下午
 * @Desc: 客户端连接创建工具
 */
@Slf4j
public class ConnectionUtils {
    static Config config = ConfigFactory.load();

    /**
     * 获取HBASE连接
     *
     * @return 返回HBASE连接
     */
    public static Connection getHbaseConnection() throws IOException {
        log.debug("HBASE连接准备创建...........");
        // 创建HBASE配置
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", config.getString(ConfigNames.HBASE_ZK_QUORUM));
        // 创建HBASE连接
        Connection conn = ConnectionFactory.createConnection(conf);
        log.debug("创建HBASE连接成功...........");
        return conn;
    }

    /**
     * 获取ClickHouse连接
     *
     * @return 返回ClickHouse连接
     */
    public static java.sql.Connection getClickHouseConnection() throws Exception {
        log.debug("ClickHouse连接准备创建...........");
        String ckDriver = config.getString(ConfigNames.CK_JDBC_DRIVER);
        String ckUrl = config.getString(ConfigNames.CK_JDBC_URL);

        Class.forName(ckDriver);
        java.sql.Connection conn = DriverManager.getConnection(ckUrl);
        log.debug("创建ClickHouse连接成功...........");
        return conn;
    }

    public static Jedis getRedisConnection() {
        String host = config.getString(ConfigNames.REDIS_HOST);
        int port = config.getInt(ConfigNames.REDIS_PORT);
        Jedis jedis = new Jedis(host, port);
        String ping = jedis.ping();
        if (StringUtils.isNoneBlank(ping)) {
            log.debug("创建Redis连接成功...........");
        } else {
            log.error("创建Redis连接失败...........");
        }
        return jedis;
    }
}

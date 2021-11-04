package org.wizard.marketing.engine.utils;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/18 12:15 下午
 * @Desc: 配置文件，参数名称统一管理类
 */
public class ConfigNames {
    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    public static final String KAFKA_AUTO_OFFSET_RESET = "kafka.auto.offset.reset";
    public static final String KAFKA_ACTION_DETAIL_TOPIC = "kafka.action_detail.topic";

    public static final String HBASE_ZK_QUORUM = "hbase.zookeeper.quorum";
    public static final String HBASE_PROFILE_TABLE = "hbase.profile.table";

    public static final String CK_JDBC_DRIVER = "ck.jdbc.driver";
    public static final String CK_JDBC_URL = "ck.jdbc.url";

    public static final String REDIS_HOST = "redis.host";
    public static final String REDIS_PORT = "redis.port";
}

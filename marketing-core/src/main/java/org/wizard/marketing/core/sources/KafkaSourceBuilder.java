package org.wizard.marketing.core.sources;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.wizard.marketing.core.common.constant.LoadConstant;

import java.util.Properties;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/18 11:24 上午
 * @Desc: 各类kafka source的构建工具
 */
public class KafkaSourceBuilder {
    Config config;

    public KafkaSourceBuilder() {
        config = ConfigFactory.load();
    }

    public FlinkKafkaConsumer<String> build(String topic) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", config.getString(LoadConstant.KAFKA_BOOTSTRAP_SERVERS));
        props.setProperty("auto.offset.reset", config.getString(LoadConstant.KAFKA_AUTO_OFFSET_RESET));

        return new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);
    }
}

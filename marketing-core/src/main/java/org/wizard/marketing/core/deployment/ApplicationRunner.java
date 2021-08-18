package org.wizard.marketing.core.deployment;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.wizard.marketing.core.source.KafkaSourceBuilder;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/18 11:11 上午
 * @Desc: 营销系统启动程序
 */
public class ApplicationRunner {
    public static void main(String[] args) throws Exception {
        LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(new Configuration());

        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();
        DataStreamSource<String> stream = env.addSource(kafkaSourceBuilder.build("ActionLog"));
        stream.print();

        env.execute();
    }
}

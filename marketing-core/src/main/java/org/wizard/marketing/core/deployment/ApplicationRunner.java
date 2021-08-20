package org.wizard.marketing.core.deployment;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.functions.JsonToBeanMapFunction;
import org.wizard.marketing.core.sources.KafkaSourceBuilder;

import java.util.Objects;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/18 11:11 上午
 * @Desc: 营销系统启动程序
 */
public class ApplicationRunner {
    public static void main(String[] args) throws Exception {
        LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(new Configuration());

        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();
        DataStream<String> stream = env.addSource(kafkaSourceBuilder.build("ActionLog"));

        DataStream<EventBean> streamWithBean = stream.map(new JsonToBeanMapFunction()).filter(Objects::nonNull);

        KeyedStream<EventBean, String> keyedStream = streamWithBean.keyBy(EventBean::getDeviceId);



        env.execute();
    }
}

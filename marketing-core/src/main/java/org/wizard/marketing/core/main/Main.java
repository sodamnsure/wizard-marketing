package org.wizard.marketing.core.main;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.wizard.marketing.core.beans.EventBean;
import org.wizard.marketing.core.beans.ResultBean;
import org.wizard.marketing.core.functions.DynamicAllocateFunction;
import org.wizard.marketing.core.functions.JsonToBeanFunction;
import org.wizard.marketing.core.functions.KafkaSourceBuilder;
import org.wizard.marketing.core.functions.RuleMatchFunction;

import java.time.Duration;
import java.util.Objects;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/5 11:06 上午
 * @Desc: 主类
 */
public class Main {
    public static void main(String[] args) throws Exception {
        /*
          构建env
         */
        LocalStreamEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(new Configuration());
        env.setParallelism(1);

        /*
          读取kafka中的用户行为日志
         */
        KafkaSourceBuilder kafkaSourceBuilder = new KafkaSourceBuilder();
        DataStream<String> stream = env.addSource(kafkaSourceBuilder.build());

        /*
          Json解析
         */
        DataStream<EventBean> streamWithBean = stream.map(new JsonToBeanFunction()).filter(Objects::nonNull);

        /*
          分配水印
         */
        WatermarkStrategy<EventBean> watermarkStrategy = WatermarkStrategy.<EventBean>forBoundedOutOfOrderness(Duration.ofMillis(0))
                .withTimestampAssigner((SerializableTimestampAssigner<EventBean>) (eventBean, l) -> eventBean.getTimeStamp());
        SingleOutputStreamOperator<EventBean> streamWithWatermark = streamWithBean.assignTimestampsAndWatermarks(watermarkStrategy);

        streamWithWatermark.process(new DynamicAllocateFunction());

        /*
          选取DeviceId作为Key
         */
        KeyedStream<EventBean, String> keyedStream = streamWithWatermark.keyBy(EventBean::getDeviceId);

        /*
          规则计算
         */
        SingleOutputStreamOperator<ResultBean> process = keyedStream.process(new RuleMatchFunction());

        process.print();

        env.execute();
    }
}

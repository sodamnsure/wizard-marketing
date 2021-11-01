package org.wizard.marketing.core.datagen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.wizard.marketing.core.beans.EventBean;

import java.util.HashMap;
import java.util.Properties;

/**
 * @Author: sodamnsure
 * @Date: 2021/9/9 3:19 下午
 * @Desc:
 */
@Slf4j
public class ActionLogGen {
    public static void main(String[] args) {
        log.info("开始生产数据");
        // 创建多线程，并行执行，创建Runnable匿名内部类
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ObjectMapper mapper = new ObjectMapper();
                    // 配置kafka
                    Properties props = new Properties();
                    props.setProperty("bootstrap.servers", "feelings:9093");
                    props.put("acks", "all");
                    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                    KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
                    while (true) {
                        EventBean logBean = new EventBean();
                        // logBean设置account: 生成1到10000之间的随机数，不足6位补齐0
                        String account = StringUtils.leftPad(RandomUtils.nextInt(1, 100000) + "", 6, "0");
                        logBean.setAccount(account);
                        // logBean设置appId
                        logBean.setAppId("com.feelings.marketing");
                        // logBean设置版本
                        logBean.setAppVersion("2.5");
                        // logBean设置运营商
                        logBean.setCarrier("中国移动");
                        // logBean设置设备ID
                        logBean.setDeviceId(account);
                        // logBean设置设备类型
                        logBean.setDeviceType("mi6");
                        // logBean设置IP
                        logBean.setIp("172.247.129.103");
                        // logBean设置经度
                        logBean.setLatitude(RandomUtils.nextDouble(120.0, 160.0));
                        // logBean设置纬度:
                        logBean.setLongitude(RandomUtils.nextDouble(10.0, 52.0));
                        // logBean设置网络类型
                        logBean.setNetType("5G");
                        // logBean设置操作系统
                        logBean.setOsName("android");
                        // logBean设置操作系统版本
                        logBean.setOsVersion("7.5");
                        // logBean设置发布渠道
                        logBean.setReleaseChannel("应用宝");
                        // logBean设置分辨率
                        logBean.setResolution("2048*1024");
                        // logBean设置sessionId: 随机生成最小长度最大长度为10的字符串
                        logBean.setSessionId(RandomStringUtils.randomNumeric(10, 10));
                        // logBean设置timeStamp
                        logBean.setTimeStamp(System.currentTimeMillis());
                        // logBean设置eventId: 随机生成26个英文字母当中的一个
                        logBean.setEventId("K");
                        // logBean设置properties
                        HashMap<String, String> properties = new HashMap<>();
                        for (int i = 0; i < RandomUtils.nextInt(1, 5); i++) {
                            properties.put("p" + RandomUtils.nextInt(1, 10), "v1");
                        }
                        logBean.setProperties(properties);

                        String log = null;
                        try {
                            log = mapper.writeValueAsString(logBean);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        // System.out.println(log);

                        // 写入kafka的topic，封装log
                        ProducerRecord<String, String> record = new ProducerRecord<>("test", log);
                        kafkaProducer.send(record);

                        // 在run方法中是不能抛异常的
                        try {
                            Thread.sleep(RandomUtils.nextInt(100, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}

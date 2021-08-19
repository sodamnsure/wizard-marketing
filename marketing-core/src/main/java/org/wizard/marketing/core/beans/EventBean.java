package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 10:19 上午
 * @Desc: 事件对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventBean {
    private String account;
    private String appId;
    private String appVersion;
    private String carrier;
    private String deviceId;
    private String deviceType;
    private String ip;
    private Double latitude;
    private Double longitude;
    private String netType;
    private String osName;
    private String osVersion;
    private String releaseChannel;
    private String resolution;
    private String sessionId;
    private Long timeStamp;
    private String eventId;
    private Map<String, String> properties;
}

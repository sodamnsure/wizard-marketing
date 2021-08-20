package org.wizard.marketing.core.beans;

import lombok.Getter;
import lombok.Setter;
import org.wizard.marketing.core.beans.basic.Unit;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 10:19 上午
 * @Desc: 事件对象
 */
@Getter
@Setter
public class EventBean extends Unit {
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
}

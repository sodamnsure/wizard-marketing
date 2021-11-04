package org.wizard.marketing.engine.beans;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/8/19 10:19 上午
 * @Desc: 事件对象
 */
@Getter
@Setter
public class EventBean {
    /**
     * 账号
     */
    private String account;

    /**
     * APP ID
     */
    private String appId;

    /**
     * APP版本
     */
    private String appVersion;

    /**
     * 运营商
     */
    private String carrier;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 用户ip
     */
    private String ip;

    /**
     * 维度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 网络类型
     */
    private String netType;

    /**
     * 系统
     */
    private String osName;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * 发行渠道
     */
    private String releaseChannel;

    /**
     * 分辨率
     */
    private String resolution;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 时间戳
     */
    private Long timeStamp;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件属性
     */
    private Map<String, String> properties;
}

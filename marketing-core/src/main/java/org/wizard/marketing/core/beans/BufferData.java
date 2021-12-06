package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/3 11:45 AM
 * @Desc: 缓存返回结果封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class BufferData {
    /**
     * 账号
     */
    private String deviceId;

    /**
     * 缓存ID
     */
    private String cacheId;

    /**
     * 缓存有效时间起始
     */
    private long timeRangeStart;

    /**
     * 缓存有效时间结束
     */
    private long timeRangeEnd;

    /**
     * 缓存返回的关心事件列表
     */
    private String value;

    public static BufferData of(String bufferKey, String value) {
        BufferData bufferData = new BufferData();
        try {
            String[] split = bufferKey.split(":");

            bufferData.setDeviceId(split[0]);
            bufferData.setCacheId(split[1]);
            bufferData.setTimeRangeStart(Long.parseLong(split[2]));
            bufferData.setTimeRangeEnd(Long.parseLong(split[3]));
            bufferData.setValue(value);
        } catch (Exception e) {
            log.error("缓存数据构造失败, bufferKey:{}, value:{}", bufferKey, value);
        }

        return bufferData;

    }

    public String getBufferKey() {
        return this.getDeviceId() + ":" + this.getCacheId() + ":" + this.getTimeRangeStart() + ":" + this.getTimeRangeEnd();
    }
}

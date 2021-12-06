package org.wizard.marketing.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.beans.BufferData;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/6 7:42 PM
 * @Desc: 缓存工具类
 */
@Slf4j
public class BufferUtils {
    public static String genBufferKey(String deviceId, String cacheId) {
        return deviceId + ":" + cacheId;
    }

    public static String genBufferValue(String seqStr, long timeRangeStart, long timeRangeEnd) {
        return seqStr + ":" + timeRangeStart + ":" + timeRangeEnd;
    }

    /**
     * 返回BufferData
     *
     * @param bufferKey deviceId:cacheId
     * @param value     seqStr:startTime:endTime
     * @return BufferData
     */
    public static BufferData of(String bufferKey, String value) {
        BufferData bufferData = new BufferData();
        try {
            String[] keyFields = bufferKey.split(":");

            bufferData.setDeviceId(keyFields[0]);
            bufferData.setCacheId(keyFields[1]);

            String[] valueFields = value.split(":");
            bufferData.setSeqStr(valueFields[0]);
            bufferData.setTimeRangeStart(Long.parseLong(valueFields[1]));
            bufferData.setTimeRangeEnd(Long.parseLong(valueFields[2]));
        } catch (Exception e) {
            log.error("缓存数据构造失败, bufferKey:{}, value:{}", bufferKey, value);
        }

        return bufferData;

    }
}

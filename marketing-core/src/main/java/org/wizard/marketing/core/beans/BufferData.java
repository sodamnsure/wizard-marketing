package org.wizard.marketing.core.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.wizard.marketing.core.utils.BufferUtils;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/3 11:45 AM
 * @Desc: 缓存返回结果封装对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
     * 缓存返回的关心事件列表字符串
     */
    private String seqStr;

    public String getBufferKey() {
        return BufferUtils.genBufferKey(this.deviceId, this.cacheId);
    }

    public String getBufferValue() {
        return BufferUtils.genBufferValue(this.seqStr, this.timeRangeStart, this.timeRangeEnd);
    }
}

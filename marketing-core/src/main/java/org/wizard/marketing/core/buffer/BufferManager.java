package org.wizard.marketing.core.buffer;

import org.wizard.marketing.core.beans.BufferData;

import java.util.Map;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:13 下午
 * @Desc: 缓存处理工具
 */
public interface BufferManager {

    BufferData getDataFromBuffer(String bufferKey);

    boolean putDataToBuffer(String bufferKey, Map<String, String> valueMap);

    void deleteBufferKey(String bufferKey, String key);
}

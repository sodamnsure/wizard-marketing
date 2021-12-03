package org.wizard.marketing.core.buffer;

import org.wizard.marketing.core.beans.BufferData;

/**
 * @Author: sodamnsure
 * @Date: 2021/11/8 6:13 下午
 * @Desc: 缓存处理工具
 */
public interface BufferManager {

    BufferData getDataFromBuffer(String bufferKey);

    boolean putDataToBuffer(BufferData bufferData);

    boolean putDataToBuffer(String bufferKey, String value);
}

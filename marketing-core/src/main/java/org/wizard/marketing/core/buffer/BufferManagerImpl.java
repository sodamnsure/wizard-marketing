package org.wizard.marketing.core.buffer;

import org.wizard.marketing.core.beans.BufferData;
import org.wizard.marketing.core.utils.ConnectionUtils;
import redis.clients.jedis.Jedis;

/**
 * @Author: sodamnsure
 * @Date: 2021/12/3 11:53 AM
 * @Desc: 缓存管理工具
 */
public class BufferManagerImpl implements BufferManager {
    Jedis jedis;

    public BufferManagerImpl() {
        jedis = ConnectionUtils.getRedisConnection();
    }

    @Override
    public BufferData getDataFromBuffer(String bufferKey) {
        String value = jedis.get(bufferKey);
        BufferData bufferData = new BufferData();

        return null;
    }

    @Override
    public boolean putDataToBuffer(BufferData bufferData) {
        return false;
    }

    public boolean putDataToBuffer(String bufferKey, String value) {
        return false;
    }
}

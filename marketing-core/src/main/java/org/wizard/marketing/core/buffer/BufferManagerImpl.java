package org.wizard.marketing.core.buffer;

import org.wizard.marketing.core.beans.BufferData;
import org.wizard.marketing.core.utils.ConnectionUtils;
import redis.clients.jedis.Jedis;

import java.util.Map;

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
        Map<String, String> valueMap = jedis.hgetAll(bufferKey);

        String[] split = bufferKey.split(":");
        String deviceId = split[0];
        String cacheId = split[1];

        return new BufferData(deviceId, cacheId, valueMap);
    }

    @Override
    public boolean putDataToBuffer(BufferData bufferData) {
        String res = jedis.hmset(bufferData.getDeviceId() + ":" + bufferData.getCacheId(), bufferData.getValueMap());

        return "OK".equals(res);
    }

    @Override
    public boolean putDataToBuffer(String bufferKey, Map<String, String> valueMap) {
        String res = jedis.hmset(bufferKey, valueMap);

        return "OK".equals(res);
    }

    @Override
    public void deleteBufferKey(String bufferKey, String key) {
        jedis.hdel(bufferKey, key);
    }
}

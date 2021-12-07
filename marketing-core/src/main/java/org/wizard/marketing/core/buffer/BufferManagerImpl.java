package org.wizard.marketing.core.buffer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.kafka.common.protocol.types.Field;
import org.wizard.marketing.core.beans.BufferData;
import org.wizard.marketing.core.constants.InitialConfigConstants;
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
    long period;

    public BufferManagerImpl() {
        jedis = ConnectionUtils.getRedisConnection();
        Config config = ConfigFactory.load();
        period = config.getLong(InitialConfigConstants.REDIS_BUFFER_PERIOD);
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
}

package org.wizard.marketing.core.buffer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.wizard.marketing.core.beans.BufferData;
import org.wizard.marketing.core.constants.InitialConfigConstants;
import org.wizard.marketing.core.utils.BufferUtils;
import org.wizard.marketing.core.utils.ConnectionUtils;
import redis.clients.jedis.Jedis;

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
        String value = jedis.get(bufferKey);

        return BufferUtils.of(bufferKey, value);
    }

    @Override
    public boolean putDataToBuffer(BufferData bufferData) {
        try {
            jedis.psetex(bufferData.getBufferKey(), period, bufferData.getBufferValue());
        } catch (Exception e) {
            jedis.close();
            jedis = ConnectionUtils.getRedisConnection();

            return false;
        }

        return true;
    }

    public boolean putDataToBuffer(String bufferKey, String value) {
        try {
            jedis.psetex(bufferKey, period, value);
        } catch (Exception e) {
            jedis.close();
            jedis = ConnectionUtils.getRedisConnection();

            return false;
        }

        return true;
    }
}

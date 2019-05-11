package wang.huaiyu.lock.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.huaiyu.lock.biz.RedisBo;
import wang.huaiyu.lock.service.DistributeLock;

@Service
public class DistributeLockImpl implements DistributeLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeLockImpl.class);

    @Autowired
    private RedisBo redisBo;

    @Override
    public boolean lock(String key, String value) {
        boolean lock = redisBo.lock(key, value, RedisBo.LOCK_EXPIRE);
        if (lock) {
            LOGGER.info("{}-{} lock success", key, value);
            return true;
        }
        long begin = System.currentTimeMillis();
        long duration = 0;
        while (duration < RedisBo.LOCK_SPIN) {
            try {
                Thread.sleep(RedisBo.LOCK_WAIT);
            } catch (InterruptedException e) {
                // DO NOTHING
            }
            if (redisBo.lock(key, value, RedisBo.LOCK_EXPIRE)) {
                LOGGER.info("{}-{} lock success use {} ms", key, value, duration);
                return true;
            }
            duration += System.currentTimeMillis() - begin;
        }
        LOGGER.error("{}-{} lock fail", key, value);
        return false;
    }

    @Override
    public void unlock(String key, String value) {
        redisBo.unlock(key, value);
    }
}

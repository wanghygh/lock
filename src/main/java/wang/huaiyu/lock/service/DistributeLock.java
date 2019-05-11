package wang.huaiyu.lock.service;

public interface DistributeLock {

    /**
     * 加锁
     *
     * @param key   键
     * @param value 值
     * @return 是否成功
     */
    boolean lock(String key, String value);

    /**
     * 解锁
     *
     * @param key   键
     * @param value 值
     */
    void unlock(String key, String value);
}

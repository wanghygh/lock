package wang.huaiyu.lock.biz;

public interface RedisBo {

    /**
     * 锁过期时间(秒)
     */
    long LOCK_EXPIRE = 10L;

    /**
     * 锁自旋时间(毫秒)
     */
    long LOCK_SPIN = 5 * 1000L;

    /**
     * 锁等待时间(毫秒)
     */
    long LOCK_WAIT = 20L;

    /**
     * 加锁
     *
     * @param key    键
     * @param id     唯一标识
     * @param expire 过期时间
     * @return 是否成功
     */
    boolean lock(String key, String id, Long expire);

    /**
     * 解锁
     *
     * @param key 键
     * @param id  唯一标识
     * @return 是否成功
     */
    boolean unlock(String key, String id);

    /**
     * 获取
     *
     * @param key 键
     * @return
     */
    String get(String key);
}

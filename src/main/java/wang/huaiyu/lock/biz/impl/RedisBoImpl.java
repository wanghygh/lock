package wang.huaiyu.lock.biz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import wang.huaiyu.lock.biz.RedisBo;

import java.nio.charset.Charset;
import java.util.Objects;

@Component
public class RedisBoImpl implements RedisBo {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 解锁LUA脚本
     */
    private static final String UNLOCK_LUA_SCRIPT;

    static {
        StringBuilder unlockBuilder = new StringBuilder();
        unlockBuilder.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        unlockBuilder.append("then ");
        unlockBuilder.append("    return redis.call(\"del\",KEYS[1]) ");
        unlockBuilder.append("else ");
        unlockBuilder.append("    return 0 ");
        unlockBuilder.append("end ");
        UNLOCK_LUA_SCRIPT = unlockBuilder.toString();
    }

    @Override
    public boolean lock(String key, String id, Long expire) {
        return (boolean) redisTemplate.execute((RedisCallback) connection -> {
            RedisStringCommands commands = connection.stringCommands();
            return commands.set(key.getBytes(), id.getBytes(), Expiration.seconds(expire), RedisStringCommands.SetOption.SET_IF_ABSENT);
        });
    }

    @Override
    public boolean unlock(String key, String id) {
        return (boolean) redisTemplate.execute((RedisCallback) connection -> connection.eval(UNLOCK_LUA_SCRIPT.getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(Charset.forName("UTF-8")), id.getBytes(Charset.forName("UTF-8"))));
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.execute((RedisCallback) connection -> new String(Objects.requireNonNull(connection.get(key.getBytes())), Charset.forName("UTF-8")));
    }
}

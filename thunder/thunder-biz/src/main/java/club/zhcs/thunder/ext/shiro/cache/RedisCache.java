package club.zhcs.thunder.ext.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;

/**
 * 
 * @author kerbores@gmail.com
 *
 * @param <K>
 * @param <V>
 */
public class RedisCache<K, V> implements Cache<K, V> {

	private static final Log log = Logs.get();

	private String name;
	private byte[] nameByteArray;

	protected CacheSerializer serializer = new DefaultJdkSerializer();

	protected boolean debug = true;

	RedisConnectionFactory connectionFactory;

	/**
	 * @param name
	 * @param connectionFactory
	 */
	public RedisCache(String name, RedisConnectionFactory connectionFactory) {
		super();
		this.name = name;
		this.nameByteArray = name.getBytes();
		this.connectionFactory = connectionFactory;
	}

	@Override
	public V get(K key) {
		if (debug)
			log.debugf("HGET name=%s key=%s", name, key);
		RedisConnection conn = null;
		byte[] buf = null;
		try {
			conn = jedis();
			buf = conn.hGet(nameByteArray, genKey(key));
			if (buf == null)
				return null;
			return (V) serializer.toObject(buf);
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public V put(K key, V value) {
		if (debug)
			log.debugf("HSET name=%s key=%s", name, key);
		RedisConnection conn = null;
		try {
			conn = jedis();
			conn.hSet(nameByteArray, genKey(key), (byte[]) serializer.fromObject(value));
			return null;
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public V remove(K key) {
		if (debug)
			log.debugf("HDEL name=%s key=%s", name, key);
		RedisConnection conn = null;
		try {
			conn = jedis();
			conn.hDel(nameByteArray, genKey(key));
			return null;
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public void clear() {
		if (debug)
			log.debugf("DEL name=%s", name);
		RedisConnection conn = null;
		try {
			conn = jedis();
			conn.del(nameByteArray);
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public int size() {
		if (debug)
			log.debugf("HLEN name=%s", name);
		RedisConnection conn = null;
		try {
			conn = jedis();
			return conn.hLen(nameByteArray).intValue();
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public Set<K> keys() {
		if (debug)
			log.debugf("HKEYS name=%s", name);
		RedisConnection conn = null;
		try {
			conn = jedis();
			return (Set<K>) conn.hKeys(nameByteArray);
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	@Override
	public Collection<V> values() {
		if (debug)
			log.debugf("HVALES name=%s", name);
		RedisConnection conn = null;
		try {
			conn = jedis();
			Collection<byte[]> vals = conn.hVals(nameByteArray);
			List<V> list = new ArrayList<V>();
			for (byte[] buf : vals)
				list.add((V) serializer.fromObject(buf));
			return list;
		} finally {
			RedisConnectionUtils.releaseConnection(conn, connectionFactory);
		}
	}

	protected byte[] genKey(Object key) {
		return key.toString().getBytes();
	}

	protected RedisConnection jedis() {
		return RedisConnectionUtils.bindConnection(connectionFactory);
	}

	public RedisCache<K, V> setDebug(boolean debug) {
		this.debug = debug;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public RedisCache<K, V> setSerializer(CacheSerializer serializer) {
		this.serializer = serializer;
		return this;
	}

}

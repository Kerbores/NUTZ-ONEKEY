//package club.zhcs.thunder.ext.shiro.cache;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
//import org.apache.shiro.cache.Cache;
//import org.apache.shiro.cache.CacheException;
//import org.apache.shiro.cache.CacheManager;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//
///**
// * 
// * @author kerbores@gmail.com
// *
// */
//public class RedisCacheManager implements CacheManager {
//
//	private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<String, Cache>();
//
//	RedisConnectionFactory connectionFactory;
//
//	/**
//	 * @param connectionFactory
//	 */
//	public RedisCacheManager(RedisConnectionFactory connectionFactory) {
//		super();
//		this.connectionFactory = connectionFactory;
//	}
//
//	/**
//	 * @return the connectionFactory
//	 */
//	public RedisConnectionFactory getConnectionFactory() {
//		return connectionFactory;
//	}
//
//	/**
//	 * @param connectionFactory
//	 *            the connectionFactory to set
//	 */
//	public void setConnectionFactory(RedisConnectionFactory connectionFactory) {
//		this.connectionFactory = connectionFactory;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see org.apache.shiro.cache.CacheManager#getCache(java.lang.String)
//	 */
//	@Override
//	public <K, V> Cache<K, V> getCache(String name) throws CacheException {
//		Cache c = caches.get(name);
//
//		if (c == null) {
//			c = new RedisCache<>(name, connectionFactory);
//			caches.put(name, c);
//		}
//		return c;
//	}
//
//}

package club.zhcs.thunder.ext.shiro.cache;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public interface CacheSerializer {

	/**
	 * 如果对象无法序列化,返回null
	 */
	Object fromObject(Object obj);

	/**
	 * 要求: 如果对象无法还原,返回null
	 */
	Object toObject(Object obj);

}

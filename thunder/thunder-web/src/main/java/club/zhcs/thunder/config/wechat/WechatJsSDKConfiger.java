package club.zhcs.thunder.config.wechat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.impl.WxApi2Impl;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * 
 * @author kerbores
 *
 */
public class WechatJsSDKConfiger {

	@Autowired
	WxApi2Impl api;

	/**
	 * url 和配置的一个缓存
	 */
	LoadingCache<String, NutMap> cache;

	private String[] apis = "onMenuShareTimeline,onMenuShareAppMessage,onMenuShareQQ,onMenuShareWeibo,onMenuShareQZone,startRecord,stopRecord,onVoiceRecordEnd,playVoice,pauseVoice,stopVoice,onVoicePlayEnd,uploadVoice,downloadVoice,chooseImage,previewImage,uploadImage,downloadImage,translateVoice,getNetworkType,openLocation,getLocation,hideOptionMenu,showOptionMenu,hideMenuItems,showMenuItems,hideAllNonBaseMenuItem,showAllNonBaseMenuItem,closeWindow,scanQRCode,chooseWXPay,openProductSpecificView,addCard,chooseCard,openCard"
			.split(",");

	Log log = Logs.get();

	/**
	 * 获取 url 对应的 jssdk 配置信息
	 * 
	 * @param url
	 *            URL地址
	 * @return 配置对象
	 * @throws ExecutionException
	 *             当缓存调用出现问题时
	 */
	public NutMap getConfig(String url) throws ExecutionException {
		return getCache().get(url);
	}

	/**
	 * 获取缓存对象
	 * 
	 * @return
	 */
	public synchronized LoadingCache<String, NutMap> getCache() {
		if (cache == null) {
			cache = get();
		}
		return cache;
	}

	/**
	 * 缓存
	 * 
	 * @return
	 */
	private LoadingCache<String, NutMap> get() {
		// 比微信的缓存时间短那么一点点儿
		return CacheBuilder.newBuilder().maximumSize(2000).expireAfterAccess(7000, TimeUnit.SECONDS).removalListener(new RemovalListener<String, NutMap>() {

			@Override
			public void onRemoval(RemovalNotification<String, NutMap> notification) {
				log.debug(notification.getKey() + " removed....");
			}
		}).build(new CacheLoader<String, NutMap>() {

			@Override
			public NutMap load(String key) throws Exception {
				log.debug(key + " loading.... ");
				return loadConfig(key);
			}
		});
	}

	/**
	 * 真正去获取配置信息
	 * 
	 * @param url
	 *            url 地址
	 * @return
	 */
	protected NutMap loadConfig(String url) {
		log.debug("Generating WX JsSDKConfig using the key url -> " + url);
		return api.genJsSDKConfig(url, apis);
	}
}

package club.zhcs.thunder.config.wechat;

import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.impl.WxApi2Impl;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.cache.LoadingCache;

/**
 * 
 * @author kerbores@gmail.com
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
	 * 真正去获取配置信息
	 * 
	 * @param url
	 *            url 地址
	 * @return
	 */
	public NutMap loadConfig(String url) {
		log.debug("Generating WX JsSDKConfig using the key url -> " + url);
		return api.genJsSDKConfig(url, apis);
	}
}

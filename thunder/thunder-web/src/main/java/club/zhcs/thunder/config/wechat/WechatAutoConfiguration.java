package club.zhcs.thunder.config.wechat;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.impl.WxApi2Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Configuration
@ConditionalOnClass(WxApi2Impl.class)
@EnableConfigurationProperties(WechatConfigProperties.class)
public class WechatAutoConfiguration {
	@Autowired
	private WechatConfigProperties wechatConfigProperties;


	Log log = Logs.get();

	@Bean
	public WechatJsSDKConfiger jsConfiger() {
		return new WechatJsSDKConfiger();
	}

	@Bean(name = "wxapi")
	public WxApi2Impl api() {
		WxApi2Impl api = new WxApi2Impl(
				wechatConfigProperties.getToken(),
				wechatConfigProperties.getAppid(),
				wechatConfigProperties.getAppsecret(),
				null,
				wechatConfigProperties.getEncodingAesKey());
		return api;
	}

}

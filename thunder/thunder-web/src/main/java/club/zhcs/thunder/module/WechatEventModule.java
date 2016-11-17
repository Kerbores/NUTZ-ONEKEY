package club.zhcs.thunder.module;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Cnd;
import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.weixin.bean.WxInMsg;
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.impl.AbstractWxHandler;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.repo.com.qq.weixin.mp.aes.AesException;
import org.nutz.weixin.repo.com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.nutz.weixin.spi.WxHandler;
import org.nutz.weixin.spi.WxResp;
import org.nutz.weixin.util.Wxs;

import club.zhcs.thunder.bean.qa.Nutzer;
import club.zhcs.thunder.biz.qa.NutzerService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;

/**
 * @author 王贵源
 * @description 绑定
 * @Copyright 内部代码,禁止转发
 * @date 2016年1月7日 下午9:17:12
 *
 */
@Filters
public class WechatEventModule extends AbstractBaseModule {

	@Inject("wxApi")
	WxApi2Impl api;

	@Inject
	PropertiesProxy config;

	@Inject
	private NutzerService nutzerService;

	{
		Wxs.enableDevMode();
	}

	/**
	 * 只注册我们关心的事件（视频，语音等消息暂时一概忽略）
	 */
	protected WxHandler wxHandler = new AbstractWxHandler() {

		/**
		 * 微信服务号的验证
		 * 
		 * @see http://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login
		 */
		@Override
		public boolean check(String signature, String timestamp, String nonce, String key) {
			return Wxs.check(api.getToken(), signature, timestamp, nonce);
		};

		/**
		 * 默认返回的消息，应该是一个帮助类的提示文本
		 */
		@Override
		public WxOutMsg defaultMsg(WxInMsg msg) {
			return null;
		}

		/*
		 * 什么都不做，免得点菜单时各种触发defaultMsg by SK.Loda
		 * 
		 * @see
		 * com.kerbores.wx.api.impl.AbstractWxHandler#eventClick(com.kerbores
		 * .wx.bean.WxInMsg)
		 */
		@Override
		public WxOutMsg eventClick(WxInMsg msg) {
			return defaultMsg(msg);
		}

		/**
		 * 微信关注事件 1. 根据openid看看是谁,存入用户数据库 2. 首次关注，如有推荐人同时记录推荐人 3. 响应欢迎页面
		 */
		@Override
		public WxOutMsg eventSubscribe(WxInMsg msg) {
			WxResp resp = api.user_info(msg.getFromUserName(), "zh_CN");
			Nutzer nutzer = nutzerService.fetch(Cnd.where("openid", "=", msg.getFromUserName()));
			if (nutzer == null) {
				nutzer = new Nutzer();
				nutzer.setOpenid(msg.getFromUserName());
				String nickName = resp.getString("nickname").replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "");
				nutzer.setCity(resp.getString("city"));
				nutzer.setCountry(resp.getString("country"));
				nutzer.setProvince(resp.getString("province"));
				nutzer.setNickName(nickName);
				nutzer.setHeadImgUrl(resp.getString("headimgurl"));
				nutzerService.save(nutzer);
				return Wxs.respText(null, "欢迎关注!");
			} else {
				return Wxs.respText(null, "欢迎回来!");
			}

		}

		/**
		 * 取消关注事件
		 */
		@Override
		public WxOutMsg eventUnsubscribe(WxInMsg msg) {
			// 将客户状态设置为禁用
			return defaultMsg(msg);
		}

		@Override
		public WxOutMsg eventView(WxInMsg msg) {
			return defaultMsg(msg);
		}

		@Override
		public WXBizMsgCrypt getMsgCrypt() {
			try {
				return new WXBizMsgCrypt(api.getAccessToken(), api.getEncodingAesKey(), api.getAppid());
			} catch (AesException e) {
				throw new RuntimeException(e);
			}
		};

		/**
		 * 处理文字消息
		 */
		@Override
		public WxOutMsg text(WxInMsg msg) {

			try {
				NutMap m = BaiduDog.send("http://apis.baidu.com/apistore/weatherservice/cityname?cityname=" + URLEncoder.encode(msg.getContent(), "UTF-8"), NutMap.NEW(),
						METHOD.GET);
				return Wxs.respText(msg.getFromUserName(), Json.toJson(m, JsonFormat.nice()));
			} catch (UnsupportedEncodingException e) {
				log.error(e);
				return Wxs.respText(msg.getFromUserName(), "查询失败!");
			}
		}

		@Override
		public WxOutMsg voice(WxInMsg msg) {
			try {
				NutMap m = BaiduDog.send("http://apis.baidu.com/apistore/weatherservice/cityname?cityname=" + URLEncoder.encode(msg.getRecognition().replace("。", ""), "UTF-8"),
						NutMap.NEW(), METHOD.GET);
				return Wxs.respText(msg.getFromUserName(), Json.toJson(m, JsonFormat.nice()));
			} catch (UnsupportedEncodingException e) {
				log.error(e);
				return Wxs.respText(msg.getFromUserName(), "查询失败!");
			}
		}
	};

	public static class BaiduDog {
		public static NutMap send(String url, NutMap paras, METHOD method) {
			Request request = Request.create(url, method, paras, Header.create().set("apikey", "1626a6545bea326d03825400a817c8f2"));
			Response response = Sender.create(request).send();
			if (response.isOK()) {
				return Lang.map(response.getContent());
			}
			return NutMap.NEW().addv("success", false).addv("code", response.getStatus());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kerbores.nutz.module.base.AbstractBaseModule#_getNameSpace()
	 */
	@Override
	public String _getNameSpace() {
		return null;
	}

	/**
	 * 微信消息回调入口
	 * 
	 * @param key
	 * @param req
	 * @return
	 * @throws IOException
	 */
	@At({ "/wechat", "/wechat/?" })
	@Fail("http:200")
	public View msgIn(String key, HttpServletRequest req) throws IOException {
		return Wxs.handle(wxHandler, req, key);
	}

}
package club.zhcs.thunder.controller.wechat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.nutz.dao.Cnd;
import org.nutz.http.Http;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.weixin.bean.WxInMsg;
import org.nutz.weixin.bean.WxMenu;
import org.nutz.weixin.bean.WxOutMsg;
import org.nutz.weixin.impl.AbstractWxHandler;
import org.nutz.weixin.impl.WxApi2Impl;
import org.nutz.weixin.repo.com.qq.weixin.mp.aes.AesException;
import org.nutz.weixin.repo.com.qq.weixin.mp.aes.WXBizMsgCrypt;
import org.nutz.weixin.spi.WxHandler;
import org.nutz.weixin.spi.WxResp;
import org.nutz.weixin.util.Wxs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.collect.Lists;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.qa.Nutzer;
import club.zhcs.thunder.biz.qa.NutzerService;
import club.zhcs.thunder.config.wechat.NutzViewWrapper;
import club.zhcs.thunder.config.wechat.WechatConfigProperties;
import club.zhcs.thunder.config.wechat.WechatJsSDKConfiger;
import club.zhcs.thunder.controller.base.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Controller
@Api(value = "Wechat", tags = { "微信接入" })
public class WechatEventController extends BaseController {

	@Autowired
	WxApi2Impl api;

	@Autowired
	NutzerService nutzerService;

	@Autowired
	WechatJsSDKConfiger wechatJsSDKConfiger;

	@Autowired
	WechatConfigProperties wechatConfigProperties;

	{
		Wxs.enableDevMode();
	}

	protected WxHandler wxHandler = new AbstractWxHandler() {

		@Override
		public WXBizMsgCrypt getMsgCrypt() {
			try {
				return new WXBizMsgCrypt(api.getToken(), api.getEncodingAesKey(), api.getAppid());
			} catch (AesException e) {
				logger.debug(e);
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean check(String signature, String timestamp, String nonce, String key) {
			return Wxs.check(api.getToken(), signature, timestamp, nonce);
		}

		public WxOutMsg voice(WxInMsg msg) {
			return Wxs.respText(msg.getRecognition());
		};

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.nutz.weixin.impl.AbstractWxHandler#defaultMsg(org.nutz.weixin.bean.
		 * WxInMsg)
		 */
		@Override
		public WxOutMsg defaultMsg(WxInMsg msg) {
			return Wxs.respText("hello!");
		}
	};

	/**
	 * 微信继而验证和消息回调
	 * 
	 * @param key
	 * @param req
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "wechat", "wechat/?" })
	@ApiIgnore
	public View msgIn(String key, HttpServletRequest req) throws IOException {
		return new NutzViewWrapper(Wxs.handle(wxHandler, req, key));
	}

	public static enum AuthBiz {
		/**
		 * 
		 */
		LIST("列表", "/"),
		/**
		 * 
		 */
		ADD("发帖", "/add"),
		/**
		 * 
		 */
		ME("个人中心", "/me");
		String name;
		String url;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the url
		 */
		public String getUrl() {
			return url;
		}

		/**
		 * @param url
		 *            the url to set
		 */
		public void setUrl(String url) {
			this.url = url;
		}

		/**
		 * @param name
		 * @param url
		 */
		private AuthBiz(String name, String url) {
			this.name = name;
			this.url = url;
		}

	}

	/**
	 * 网页授权
	 * 
	 * @param code
	 *            微信授权code
	 * @param state
	 *            自定义state
	 * @param biz
	 *            业务类型
	 * @throws IOException
	 * @throws ServletException
	 */
	@GetMapping("auth2")
	@ApiIgnore
	public ModelAndView auth2(@RequestParam("code") String code, @RequestParam("state") String state, @RequestParam("biz") AuthBiz biz) throws ServletException, IOException {
		log.infof("args==> %s,%s,%s", code, state, biz);
		NutMap data = Lang.map(
				Http.get(String.format("https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
						api.getAppid(),
						api.getAppsecret(), code)).getContent());
		if (data.get("errcode") != null) {// 调用微信出错
			log.error("=====error msg:" + data.get("errcode") + ",error msg:" +
					data.get("errmsg") + "======");
			return null;
		}
		ModelAndView view = new ModelAndView();
		view.setView(new RedirectView(String.format("http://kerbores.ngrok.wendal.cn%s?openid=%s", biz.url, data.getString("openid"))));
		return view;
	}

	/**
	 * 创建菜单
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@GetMapping("menu")
	@ApiOperation("创建菜单")
	public @ResponseBody Result menu() throws UnsupportedEncodingException {
		List<WxMenu> menus = Lists.newArrayList();
		WxMenu list = new WxMenu();
		list.setType("view");
		list.setName("话题列表");
		list.setUrl(String.format(
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=LIST#wechat_redirect",
				api.getAppid(),
				URLEncoder.encode(String.format("%s%s?biz=%s", wechatConfigProperties.getDomain(), "auth2", AuthBiz.LIST), "UTF8")));
		list.setSubButtons(Lists.newArrayList());

		WxMenu add = new WxMenu();
		add.setType("view");
		add.setName("我要提问");
		add.setUrl(String.format(
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=ADD#wechat_redirect",
				api.getAppid(),
				URLEncoder.encode(String.format("%s%s?biz=%s", wechatConfigProperties.getDomain(), "auth2", AuthBiz.ADD), "UTF8")));
		add.setSubButtons(Lists.newArrayList());

		WxMenu me = new WxMenu();
		me.setType("view");
		me.setName("我的信息");
		me.setUrl(String.format(
				"https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=ME#wechat_redirect",
				api.getAppid(),
				URLEncoder.encode(String.format("%s%s?biz=%s", wechatConfigProperties.getDomain(), "auth2", AuthBiz.ME), "UTF8")));
		me.setSubButtons(Lists.newArrayList());
		menus.add(list);
		menus.add(add);
		menus.add(me);
		api.menu_create(menus);
		return Result.success();
	}

	/**
	 * 获取 jssdk 配置
	 * 
	 * @param url
	 * @return
	 * @throws ExecutionException
	 */
	@PostMapping("config")
	@ApiOperation("获取 JSSDK 配置")
	public @ResponseBody Result config(@RequestParam("url") @ApiParam("网页地址") String url) throws ExecutionException {
		return Result.success().addData("config", wechatJsSDKConfiger.getConfig(url));
	}

}

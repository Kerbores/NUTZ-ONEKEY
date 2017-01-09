package club.zhcs.thunder.chain;

import org.nutz.dao.Cnd;
import org.nutz.http.Http;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.weixin.impl.WxApi2Impl;

import club.zhcs.thunder.Application;
import club.zhcs.thunder.biz.qa.NutzerService;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project platform-web-customer
 *
 * @file WxUserInjectProcessor.java
 *
 * @description 微信用户注入
 *
 * @time 2016年7月27日 下午12:53:50
 *
 */
public class WxUserInjectProcessor extends AbstractProcessor {

	Log log = Logs.get();

	@Override
	public void process(ActionContext ac) throws Throwable {
		if (!Mvcs.getIoc().get(PropertiesProxy.class, "config").getBoolean("install-flag")) {
			doNext(ac);
			return;
		}
		WxApi2Impl api = ac.getIoc().get(WxApi2Impl.class, "wxApi");

		NutzerService nutzerService = ac.getIoc().get(NutzerService.class);

		if (isDebug()) {
			// 开发环境模拟一下
			String openid = "otOKDvwEbkaeI8MewpbAFWonrCp0";
			Mvcs.getReq().getSession().setAttribute("openid", openid);
			Mvcs.getReq().getSession().setAttribute(Application.SessionKeys.WECHAT_USER_KEY, nutzerService.fetch(Cnd.where("openid", "=", openid)));
			doNext(ac);
			return;
		}

		String code = Mvcs.getReq().getParameter("code");
		if (Strings.isBlank(code)) {// 没有code参数
			doNext(ac);
			return;
		}
		String wechatInterface = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + api.getAppid() + "&secret=" + api.getAppsecret() + "&code=" + code
				+ "&grant_type=authorization_code";
		log.debug("ready to invoke url : " + wechatInterface);
		String info = Http.get(wechatInterface).getContent();
		NutMap data = Lang.map(info);
		if (data.get("errcode") != null) {
			log.error("=====error msg:" + data.get("errcode") + ",error msg:" + data.get("errmsg") + "======");
			doNext(ac);
			return;
		}
		log.debug("successful invoke ,return message:\n" + data.toString());
		Mvcs.getReq().getSession().setAttribute("openid", data.getString("openid"));

		Mvcs.getReq().getSession().setAttribute(Application.SessionKeys.WECHAT_USER_KEY, nutzerService.fetch(Cnd.where("openid", "=", data.getString("openid"))));
		doNext(ac);
	}

	// 开发者本地打开方式调试
	public boolean isDebug() {
		return Strings.equalsIgnoreCase("localhost", Mvcs.getReq().getServerName()) || Strings.equalsIgnoreCase("127.0.0.1", Mvcs.getReq().getServerName())
				|| Mvcs.getIoc().get(PropertiesProxy.class, "config").getBoolean("debug");
	}

}

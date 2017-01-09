package club.zhcs.thunder.biz.config;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.mvc.Mvcs;
import org.nutz.weixin.bean.WxMenu;
import org.nutz.weixin.impl.WxApi2Impl;

import com.google.common.collect.Lists;

import club.zhcs.thunder.bean.config.WechatMenu;
import club.zhcs.thunder.bean.config.WechatMenu.Type;
import club.zhcs.titans.utils.biz.BaseService;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project platform-service
 *
 * @file WechatMenuService.java
 *
 * @description 微信菜单业务
 *
 * @time 2016年8月3日 下午10:53:27
 *
 */
public class WechatMenuService extends BaseService<WechatMenu> {

	@Inject
	WxApi2Impl wxApi;

	public List<WxMenu> exchange(List<WechatMenu> menus) {
		final List<WxMenu> target = Lists.newArrayList();
		Lang.each(menus, new Each<WechatMenu>() {

			@Override
			public void invoke(int index, WechatMenu ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				target.add(exchange(ele));
			}
		});
		return target;
	}

	public WxMenu exchange(WechatMenu menu) {
		final WxMenu target = new WxMenu();
		target.setName(menu.getName());
		if (menu.getSubMenus() == null || menu.getSubMenus().size() == 0) {
			target.setType(menu.getType().getAction());
			if (menu.getType() == Type.VIEW) {
				target.setUrl(formatAuthUrl(menu.getAction()));
			} else {
				target.setKey(menu.getAction());
			}
		}
		if (menu.getSubMenus() != null && menu.getSubMenus().size() > 0) {
			Lang.each(menu.getSubMenus(), new Each<WechatMenu>() {

				@Override
				public void invoke(int index, WechatMenu ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					if (target.getSubButtons() == null) {
						List<WxMenu> subs = Lists.newArrayList();
						target.setSubButtons(subs);
					}
					target.getSubButtons().add(exchange(ele));
				}
			});
		}
		return target;
	}

	public String formatAuthUrl(String action) {
		String appid = wxApi.getAppid();
		String domain = config.get("base.domain", "www.kerbores.com");
		String contextPath = config.get("client.context", Mvcs.getReq().getContextPath());
		String url = action.startsWith("http") ? action : String.format("http://%s/%s/%s", domain, contextPath, action);

		return String
				.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect",
						appid, url);
	}

	public List<WxMenu> getWxMenus() {
		List<WechatMenu> menus = query(Cnd.where("parentId", "=", 0).orderBy("index", "ASC"));
		Lang.each(menus, new Each<WechatMenu>() {

			@Override
			public void invoke(int index, WechatMenu menu, int length) throws ExitLoop, ContinueLoop, LoopException {
				menu.setSubMenus(query(Cnd.where("parentId", "=", menu.getId()).orderBy("index", "ASC")));
			}
		});
		return exchange(menus);
	}

}

package club.zhcs.thunder.module;

import javax.servlet.http.HttpSession;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import club.zhcs.thunder.Application.SessionKeys;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.shiro.ShiroUserService;
import club.zhcs.titans.nutz.captcha.JPEGView;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.codec.DES;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-web
 *
 * @file SystemModule.java
 *
 * @description 系统功能
 *
 * @copyright 内部代码,禁止转发
 *
 * @time 2016年5月17日 上午12:53:23
 *
 */
@At("system")
public class SystemModule extends AbstractBaseModule {

	@Inject
	ShiroUserService shiroUserService;

	@Inject
	UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kerbores.nutz.module.base.AbstractBaseModule#_getNameSpace()
	 */
	@Override
	public String _getNameSpace() {
		return "system";
	}

	@At
	@GET
	@Ok("beetl:pages/admin/pwd.html")
	public Result changePassword() {
		return Result.success();
	}

	@At
	@POST
	public Result changePassword(@Param("old") String old, @Param("new") String newPwd, @Attr(SessionKeys.USER_KEY) User user) {
		return userService.changePassword(user.getId(), old, newPwd);
	}

	// 此处前台是json请求所以会返回json串
	@At
	@Filters
	public Result login(@Param("user") String user, @Param("password") String password, @Param("captcha") String captcha, @Param("rememberMe") boolean rememberMe,
			HttpSession session) {
		if (Strings.equalsIgnoreCase(captcha, session.getAttribute(JPEGView.CAPTCHA).toString())) {
			Result result = shiroUserService.login(user, password);
			if (result.isSuccess()) {
				// 登录成功处理
				_putSession(SessionKeys.USER_KEY, result.getData().get("loginUser"));
				if (rememberMe) {
					NutMap data = NutMap.NEW();
					data.put("user", user);
					data.put("password", password);
					data.put("rememberMe", rememberMe);
					_addCookie("kerbores", DES.encrypt(Json.toJson(data)), 24 * 60 * 60 * 365);
				}
			}
			return result;
		} else {
			return Result.fail("验证码输入错误");
		}
	}

	@At
	@Ok(">>:/")
	public Result logout(HttpSession session) {
		session.invalidate();
		return Result.success();
	}

	@At
	@Ok(">>:/apm/dashboard")
	public Result main(@Attr(SessionKeys.USER_KEY) User user) {
		return Result.success();
	}

	@At
	@Ok("beetl:pages/admin/profile.html")
	public Result profile(@Attr(SessionKeys.USER_KEY) User user) {
		return Result.success().addData("user", userService.fetch(user.getId()));
	}

	@At("/settings/save")
	@POST
	public Result save(@Param("realName") String realName, @Param("phone") String phone, @Param("email") String email, @Attr(SessionKeys.USER_KEY) User user) {
		return userService.update(Chain.make("realName", realName).add("phone", phone).add("email", email), Cnd.where("id", "=", user.getId())) == 1 ? Result.success() : Result
				.fail("更新失败!");
	}

	@At
	@Ok("beetl:pages/admin/settings.html")
	public Result settings(@Attr(SessionKeys.USER_KEY) User user) {
		return Result.success().addData("user", userService.fetch(user.getId()));
	}
}

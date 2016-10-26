package club.zhcs.thunder.module;

import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.plugins.view.captcha.CaptchaView;

import club.zhcs.titans.nutz.module.base.AbstractBaseModule;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-web
 *
 * @file ViewsModule.java
 *
 * @description 视图集成示例
 *
 * @time 2016年9月7日 下午6:30:29
 *
 */
@At("/views")
@Filters
public class ViewsModule extends AbstractBaseModule {

	/**
	 * velocity 示例
	 * 
	 * @return
	 */
	// @At
	// @Ok("vm:pages/velocity/demo.html")
	// public Result velocity() {
	// return Result.success();
	// }

	/**
	 * 验证码示例
	 * 
	 * @return
	 */
	@At
	public View captcha() {
		return new CaptchaView(null, 5);
	}

}

package club.zhcs.hammer.module.log;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.log.LoginLog;
import club.zhcs.hammer.biz.log.LoginLogService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@At("trace")
@Api(name = "Trace", description = "登录日志接口")
public class LoginLogModule extends AbstractBaseModule {

	@Inject
	LoginLogService loginLogService;

	@At
	@GET
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", loginLogService.searchByPage(_fixPage(page)));
	}

	@At
	public Result search(@Param(value = "page", df = "1") int page, @Param("key") String key) {
		Pager<LoginLog> pager = loginLogService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "account", "ip");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

}

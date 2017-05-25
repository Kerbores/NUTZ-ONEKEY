package club.zhcs.hammer.module.log;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.log.OperationLog;
import club.zhcs.hammer.biz.log.OperationLogService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@At("log")
@Api(name = "Log", description = "操作日志接口")
public class OperationLogModule extends AbstractBaseModule {

	@Inject
	OperationLogService operationLogService;

	@At
	@GET
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", operationLogService.searchByPage(_fixPage(page)));
	}

	@At
	@GET
	public Result search(@Param(value = "page", df = "1") int page, @Param("key") String key) {
		Pager<OperationLog> pager = operationLogService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "account", "ip", "module", "action", "description");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

}

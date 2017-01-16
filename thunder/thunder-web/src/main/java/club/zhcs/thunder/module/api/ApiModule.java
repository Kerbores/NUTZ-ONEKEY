package club.zhcs.thunder.module.api;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugin.sigar.gather.CPUGather;
import org.nutz.plugin.sigar.gather.DISKGather;
import org.nutz.plugin.sigar.gather.Gathers;
import org.nutz.plugin.sigar.gather.MemoryGather;
import org.nutz.plugin.sigar.gather.NetInterfaceGather;
import org.nutz.plugin.sigar.gather.OSGather;
import org.nutz.plugin.sigar.integration.servlet.SigarServlet.APIType;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Result;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-web
 *
 * @file ApiModule.java
 *
 * @description API
 *
 * @time 2016年9月20日 下午9:06:05
 *
 */
@Api(author = "kerbores", name = "Common", description = " 提供一组 rest api", match = ApiMatchMode.ONLY)
@At("/api")
@Filters
public class ApiModule extends AbstractBaseModule {

	@Inject
	Dao dao;

	@At
	@Api(author = "kerbores", name = "数据库", description = "获取数据库信息", match = ApiMatchMode.ONLY, ok = {
			@ReturnKey(key = "db", description = " 数据库的 meta 信息")
	}, fail = {
			@ReturnKey(key = "reason", description = "失败原因")
	})
	public Result db() {

		return Result.success().addData("db", dao.meta());
	}

	@At
	@Api(author = "kerbores", name = "系统监控", description = "获取系统运行状况信息", match = ApiMatchMode.ONLY, params = {
			@ApiParam(index = 1, name = "type", description = "监控信息类型,可选值为CPU, DISK,NI,SYS,MEM,ALL")
	}, ok = {
			@ReturnKey(key = "sigar", description = "  收集到的心态信息"),
			@ReturnKey(key = " api", description = "  api 描述")
	}, fail = {
			@ReturnKey(key = "reason", description = "失败原因")
	})
	public Result sigar(@Param("type") APIType type) throws SigarException, InterruptedException {
		Sigar sigar = new Sigar();
		Result result = Result.success();
		type = null == type ? APIType.DEFAULT : type;
		switch (type) {
		case CPU:
			result.addData("sigar", CPUGather.gather((sigar)));
			break;
		case DISK:
			result.addData("sigar", DISKGather.gather(sigar));
			break;
		case NI:
			result.addData("sigar", NetInterfaceGather.gather(sigar));
			break;
		case SYS:
			result.addData("sigar", OSGather.init(sigar));
			break;
		case MEM:
			result.addData("sigar", MemoryGather.gather(sigar));
			break;
		case ALL:
			result.addData(Gathers.all());
			break;
		default:
			result.addData("apis", APIType.values()).addData("discription", "use type parameter to invoke apis like type=SYS");
		}
		return result;
	}
}

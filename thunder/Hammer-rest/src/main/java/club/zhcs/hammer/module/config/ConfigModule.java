package club.zhcs.hammer.module.config;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.config.Config;
import club.zhcs.hammer.biz.config.ConfigService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * @author kerbores@gmail.com
 *
 */
@At("/config")
@Api(name = "Config", description = "配置相关接口")
public class ConfigModule extends AbstractBaseModule {

	@Inject
	ConfigService configService;

	@At
	@GET
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", configService.searchByPage(_fixPage(page)));
	}

	@At
	@GET
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<Config> pager = configService.searchByKeyAndPage(key, page, "name", "description");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public Result save(Config config) {
		return configService.save(config) == null ? Result.fail("保存配置失败!") : Result.success().addData("config", config);
	}

	@At("/?")
	@GET
	public Result detail(long id) {
		return Result.success().addData("config", configService.fetch(id));
	}

	@At("/delete/?")
	@GET
	public Result delete(long id) {
		return configService.delete(id) == 1 ? Result.success() : Result.fail("删除配置失败!");
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public Result update(Config config) {
		return configService.updateIgnoreNull(config) != 1 ? Result.fail("更新配置失败!") : Result.success().addData("config", config);
	}

}

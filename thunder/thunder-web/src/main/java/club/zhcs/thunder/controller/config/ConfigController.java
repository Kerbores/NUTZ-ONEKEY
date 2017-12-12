package club.zhcs.thunder.controller.config;

import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.config.Config;
import club.zhcs.thunder.biz.config.ConfigService;
import club.zhcs.thunder.controller.base.BaseController;
import club.zhcs.thunder.ext.shiro.anno.SINORequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@RestController
@RequestMapping("config")
@Api(value = "Config", tags = { "配置模块" })
public class ConfigController extends BaseController {
	@Autowired
	ConfigService configService;

	/**
	 * 配置列表
	 * 
	 * @RequestParam page 页码
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.CONFIG_LIST)
	@ApiOperation("配置列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", configService.searchByPage(_fixPage(page), Cnd.NEW().desc("id")));
	}

	/**
	 * 配置检索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.CONFIG_LIST)
	@ApiOperation("配置检索")
	public Result search(@RequestParam("key") @ApiParam("关键词") String key, @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", configService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name", "description").addParam("key", key));
	}

	/**
	 * 添加配置
	 * 
	 * @param config
	 *            待添加配置
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.CONFIG_ADD)
	@ApiOperation("添加配置")
	public Result save(@RequestBody Config config) {
		return configService.save(config) == null ? Result.fail("保存配置失败!") : Result.success().addData("config", config);
	}

	/**
	 * 获取配置详情
	 * 
	 * @param id
	 *            配置id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(InstallPermission.CONFIG_EDIT)
	@ApiOperation("配置详情")
	public Result detail(@PathVariable("id") @ApiParam("配置id") long id) {
		return Result.success().addData("config", configService.fetch(id));
	}

	/**
	 * 删除配置
	 * 
	 * @param id
	 *            配置id
	 * @return
	 */
	@GetMapping("delete/{id}")
	@SINORequiresPermissions(InstallPermission.CONFIG_DELETE)
	@ApiOperation("删除配置")
	public Result delete(@PathVariable("id") @ApiParam("配置id") long id) {
		return configService.delete(id) == 1 ? Result.success() : Result.fail("删除配置失败!");
	}

	/**
	 * 更新配置
	 * 
	 * @param config
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.CONFIG_EDIT)
	@ApiOperation("更新配置")
	public Result update(@RequestBody Config config) {
		return configService.updateIgnoreNull(config) != 1 ? Result.fail("更新配置失败!") : Result.success().addData("config", config);
	}
}

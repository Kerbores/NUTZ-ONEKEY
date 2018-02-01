package club.zhcs.thunder.controller.acl;

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
import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.biz.acl.PermissionService;
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
@RequestMapping("permission")
@Api(value = "Permission", tags = { "权限模块" })
public class PermissionController extends BaseController {

	@Autowired
	PermissionService permissionService;

	/**
	 * 权限列表
	 * 
	 * @RequestParam page
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.PERMISSION_LIST)
	@ApiOperation("权限列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", permissionService.searchByPage(fixPage(page), Cnd.NEW().desc("id")));
	}

	/**
	 * 权限搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.PERMISSION_LIST)
	@ApiOperation("权限分页搜索")
	public Result search(@RequestParam("key") @ApiParam("搜索关键词") String key, @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", permissionService.searchByKeyAndPage(fixSearchKey(key), fixPage(page), "name", "description").addParam("key", key));
	}

	/**
	 * 添加权限
	 * 
	 * @param Permission
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.PERMISSION_ADD)
	@ApiOperation("添加权限")
	public Result save(@RequestBody Permission permission) {
		return permissionService.save(permission) == null ? Result.fail("保存权限失败!") : Result.success().addData("permission", permission);
	}

	/**
	 * 权限详情
	 * 
	 * @param id
	 *            权限id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(InstallPermission.PERMISSION_EDIT)
	@ApiOperation("权限详情")
	public Result detail(@PathVariable("id") @ApiParam("权限id") long id) {
		return Result.success().addData("permission", permissionService.fetch(id));
	}

	/**
	 * 删除全新啊
	 * 
	 * @param id
	 *            权限id
	 * @return
	 */
	@GetMapping("delete/{id}")
	@SINORequiresPermissions(InstallPermission.PERMISSION_DELETE)
	@ApiOperation("删除权限")
	public Result delete(@PathVariable("id") @ApiParam("权限id") long id) {
		return permissionService.delete(id) == 1 ? Result.success() : Result.fail("删除权限失败!");
	}

	/**
	 * 更新权限
	 * 
	 * @param Permission
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.PERMISSION_EDIT)
	@ApiOperation("更新权限")
	public Result update(@RequestBody Permission permission) {
		return permissionService.updateIgnoreNull(permission) != 1 ? Result.fail("更新权限失败!") : Result.success().addData("permission", permission);
	}

}

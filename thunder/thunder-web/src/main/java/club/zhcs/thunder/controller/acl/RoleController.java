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
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.controller.base.BaseController;
import club.zhcs.thunder.dto.GrantDTO;
import club.zhcs.thunder.ext.shiro.anno.SINORequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author kerbores
 *
 */
@RestController
@RequestMapping("role")
@Api(value = "Role", tags = { "角色模块" })
public class RoleController extends BaseController {

	@Autowired
	RoleService roleService;

	/**
	 * 列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.ROLE_LIST)
	@ApiOperation("角色列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", roleService.searchByPage(_fixPage(page), Cnd.NEW().desc("id")));
	}

	/**
	 * 搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.ROLE_LIST)
	@ApiOperation("角色分页搜索")
	public Result search(@RequestParam("key") String key, @RequestParam(value = "page", defaultValue = "1") int page) {
		return Result.success().addData("pager", roleService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name", "description").addParam("key", key));
	}

	/**
	 * 添加角色
	 * 
	 * @param role
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.ROLE_ADD)
	@ApiOperation("添加角色")
	public Result save(@RequestBody Role role) {
		return roleService.save(role) == null ? Result.fail("保存角色失败!") : Result.success().addData("role", role);
	}

	/**
	 * 角色详情
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(value = { InstallPermission.ROLE_EDIT })
	@ApiOperation("角色详情")
	public Result detail(@PathVariable("id") @ApiParam("角色id") long id) {
		return Result.success().addData("role", roleService.fetch(id));
	}

	/**
	 * 删除角色
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@GetMapping("delete/{id}")
	@SINORequiresPermissions(value = { InstallPermission.ROLE_DELETE })
	@ApiOperation("删除角色")
	public Result delete(@PathVariable("id") @ApiParam("角色id") long id) {
		return roleService.delete(id) == 1 ? Result.success() : Result.fail("删除角色失败!");
	}

	/**
	 * 更新角色
	 * 
	 * @param role
	 *            待更新角色
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.ROLE_EDIT)
	@ApiOperation("编辑角色")
	public Result update(@RequestBody Role role) {
		return roleService.updateIgnoreNull(role) != 1 ? Result.fail("更新角色失败!") : Result.success().addData("role", role);
	}

	/**
	 * 获取角色的权限设置信息
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@GetMapping("permission/{id}")
	@SINORequiresPermissions(InstallPermission.ROLE_GRANT)
	@ApiOperation("获取角色的授权信息")
	public Result permissionInfo(@PathVariable("id") @ApiParam("角色id") int id) {
		return Result.success().addData("infos", roleService.findPermissionsWithRolePowerdInfoByRoleId(id));
	}

	/**
	 * 设置角色的权限
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("grant")
	@SINORequiresPermissions(InstallPermission.ROLE_GRANT)
	@ApiOperation("为角色授权")
	public Result grantRole(@RequestBody GrantDTO dto) {
		return roleService.setPermission(dto.getGrantIds(), dto.getId());
	}

}

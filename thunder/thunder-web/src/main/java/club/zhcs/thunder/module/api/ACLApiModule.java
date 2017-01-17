package club.zhcs.thunder.module.api;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.biz.acl.PermissionService;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.shiro.ShiroUserService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Result;

@Api(author = "kerbores", name = "ACL", description = "访问权限控制的restApi", match = ApiMatchMode.ALL)
@At("/acl")
@Filters
public class ACLApiModule extends AbstractBaseModule {
	@Inject
	ShiroUserService shiroUserService;

	@Inject
	UserService userService;

	@Inject
	RoleService roleService;

	@Inject
	PermissionService permissionService;

	@At

	@Api(params = {
			@ApiParam(name = "userName", description = "用户名"),
			@ApiParam(name = "password", description = "密码")
	}, ok = {
			@ReturnKey(key = "loginUser", description = "登录成功的用户信息")
	}, fail = {
			@ReturnKey(key = "reason", description = "登录失败的原因")
	}, name = "登录")
	public Result login(String userName, String password) {
		return shiroUserService.login(userName, password);
	}

	@At
	public Result roles(String userName) {
		User user = userService.fetch(Cnd.where("name", "=", userName));
		if (user == null) {
			return Result.fail("不存在的用户");
		}
		return Result.success().addData("roles", shiroUserService.getAllRoles(user.getId()));
	}

	@At
	public Result permissions(String userName) {
		User user = userService.fetch(Cnd.where("name", "=", userName));
		if (user == null) {
			return Result.fail("不存在的用户");
		}
		return Result.success().addData("permissions", shiroUserService.getAllPermissions(user.getId()));
	}

	@At
	public Result checkRole(String userName, String roleName) {
		User user = userService.fetch(Cnd.where("name", "=", userName));
		if (user == null) {
			return Result.fail("不存在的用户");
		}
		Role role = roleService.fetch(Cnd.where("name", "=", roleName));
		if (role == null) {
			return Result.success().addData("msg", "不存在的角色");
		}
		return shiroUserService.checkRole(roleName, user.getId()) ? Result.success() : Result.fail("没有权限");
	}

	@At
	public Result checkPermission(String userName, String permissionName) {
		User user = userService.fetch(Cnd.where("name", "=", userName));
		if (user == null) {
			return Result.fail("不存在的用户");
		}
		Permission permission = permissionService.fetch(Cnd.where("name", "=", permissionName));
		if (permission == null) {
			return Result.success().addData("msg", "不存在的权限");
		}
		return shiroUserService.checkPermission(permissionName, user.getId()) ? Result.success() : Result.fail("没有权限");
	}

}

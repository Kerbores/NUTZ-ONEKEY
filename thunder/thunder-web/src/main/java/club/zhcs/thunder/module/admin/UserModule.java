package club.zhcs.thunder.module.admin;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import club.zhcs.thunder.aop.SystemLog;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-web
 *
 * @file UserModule.java
 *
 * @description 用户管理
 *
 * @time 2016年3月8日 上午10:51:26
 *
 */
@At("user")
public class UserModule extends AbstractBaseModule {

	@Inject
	UserService userService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kerbores.nutz.module.base.AbstractBaseModule#_getNameSpace()
	 */
	@Override
	public String _getNameSpace() {
		return "acl";
	}

	/**
	 * 添加用户页面
	 * 
	 * @return
	 */
	@At
	@GET
	@Ok("beetl:pages/admin/auth/user/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.USER_ADD)
	public Result add() {
		return Result.success();
	}

	/**
	 * 添加用户
	 * 
	 * @param user
	 *            待添加用户
	 * @return
	 */
	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.USER_ADD)
	public Result add(@Param("..") User user) {
		user.setPassword(Lang.md5(user.getPassword()));
		return userService.save(user) != null ? Result.success().addData("user", user) : Result.fail("添加用户失败!");
	}

	/**
	 * 删除用户
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@At("/delete/*")
	@ThunderRequiresPermissions(InstallPermission.USER_DELETE)
	@RequiresRoles("admin")
	public Result delete(int id) {
		return userService.delete(id) == 1 ? Result.success() : Result.fail("删除用户失败!");
	}

	/**
	 * 用户详情
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@At("/detail/*")
	@Ok("beetl:pages/admin/auth/user/detail.html")
	@ThunderRequiresPermissions(InstallPermission.USER_DETAIL)
	public Result detail(int id) {
		return Result.success().addData("user", userService.fetch(id));
	}

	/**
	 * 编辑用户页面
	 * 
	 * @param id
	 *            用户id
	 * @return
	 */
	@At("/edit/*")
	@GET
	@Ok("beetl:pages/admin/auth/user/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.USER_EDIT)
	public Result edit(int id) {
		return Result.success().addData("user", userService.fetch(id));
	}

	/**
	 * 编辑用户
	 * 
	 * @param user
	 *            待更新用户
	 * @return
	 */
	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.USER_EDIT)
	public Result edit(@Param("..") User user) {
		return userService.update(user, "realName", "phone", "email", "status") ? Result.success() : Result.fail("更新失败!");
	}

	/**
	 * 授权
	 * 
	 * @param id
	 * @return
	 */
	@At("/grant/*")
	@GET
	@Ok("beetl:pages/admin/auth/user/grant.html")
	@ThunderRequiresPermissions(InstallPermission.USER_GRANT)
	public Result grant(int id) {
		List<Record> records = userService.findPermissionsWithUserPowerdInfoByUserId(id);
		return Result.success().addData("records", records).addData("userId", id);
	}

	/**
	 * 授权
	 * 
	 * @param ids
	 * @param id
	 * @return
	 */
	@At
	@ThunderRequiresPermissions(InstallPermission.USER_GRANT)
	public Result grant(@Param("permissions") int[] ids, @Param("id") int id) {
		return userService.setPermission(ids, id);
	}

	/**
	 * 用户列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@Ok("beetl:pages/admin/auth/user/list.html")
	@ThunderRequiresPermissions(InstallPermission.USER_LIST)
	@SystemLog(module = "用户管理", methods = "用户列表")
	public Result list(@Param(value = "page", df = "1") int page) {

		page = _fixPage(page);
		Pager<User> pager = userService.searchByPage(page);
		pager.setUrl(_base() + "/user/list");
		return Result.success().addData("pager", pager);
	}

	/**
	 * 设置角色
	 *
	 * @param id
	 *            用户id
	 * @return
	 */
	@At("/role/*")
	@GET
	@ThunderRequiresPermissions(InstallPermission.USER_ROLE)
	@Ok("beetl:pages/admin/auth/user/role.html")
	@SystemLog(module = "用户管理", methods = "设置角色")
	public Result role(int id) {
		List<Record> records = userService.findRolesWithUserPowerdInfoByUserId(id);
		return Result.success().addData("records", records).addData("userId", id);
	}

	/**
	 * 设置角色
	 *
	 * @param ids
	 *            角色ID数组
	 * @param id
	 *            用户id
	 * @return
	 */
	@At
	@ThunderRequiresPermissions(InstallPermission.USER_ROLE)
	public Result role(@Param("roles") int[] ids, @Param("id") int id) {
		return userService.setRole(ids, id);
	}

	/**
	 * 搜索用户
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@Ok("beetl:pages/admin/auth/user/list.html")
	@ThunderRequiresPermissions(InstallPermission.USER_LIST)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<User> pager = userService.searchByKeyAndPage(key, page, "name", "nickName", "realName");
		pager.setUrl(_base() + "/user/search");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

}

package club.zhcs.hammer.biz.acl;

import java.util.Collections;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Lang;

import club.zhcs.hammer.bean.acl.Role;
import club.zhcs.hammer.bean.acl.RolePermission;
import club.zhcs.titans.utils.biz.BaseService;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-biz
 *
 * @file RoleService.java
 *
 * @description 角色业务
 *
 * @time 2016年3月8日 上午10:51:26
 *
 */
public class RoleService extends BaseService<Role> {

	@Inject
	RolePermissionService rolePermissionService;

	/**
	 * @author 王贵源
	 * @param id
	 * @return
	 */
	public List<Record> findPermissionsWithRolePowerdInfoByRoleId(int id) {
		Sql sql = dao().sqls().create("find.permissions.with.role.powered.info.by.role.id");
		sql.params().set("id", id);
		return search(sql);
	}

	/**
	 * 用户的全部角色
	 * 
	 * @author 王贵源
	 * @param id
	 *            用户id
	 * @return 用户的角色列表
	 */
	public List<Role> listByUserId(int id) {
		Sql sql = dao().sqls().create("list.role.by.user.id");
		sql.params().set("userId", id);
		return searchObj(sql);
	}

	/**
	 * @author 王贵源
	 * @param ids
	 * @param roleId
	 * @return
	 */
	public Result setPermission(int[] ids, int roleId) {
		/**
		 * 1.查询全部权限列表<br>
		 * 2.遍历权限.如果存在,则更新时间.如果不存在则删除,处理之后从目标数组中移除;<br>
		 * 3.遍历余下的目标数组
		 */
		if (ids == null) {
			ids = new int[] {};
		}
		List<Integer> newIds = Lang.array2list(ids, Integer.class);
		Collections.sort(newIds);
		List<RolePermission> rolePermissions = rolePermissionService.query(Cnd.where("roleId", "=", roleId));
		for (RolePermission role : rolePermissions) {
			int i = 0;
			if ((i = Collections.binarySearch(newIds, role.getPermissionId())) >= 0) {
				newIds.remove(i);
				rolePermissionService.update(role);
			} else {
				rolePermissionService.delete(role.getId());
			}
		}
		for (int pid : newIds) {
			RolePermission rolep = new RolePermission();
			rolep.setRoleId(roleId);
			rolep.setPermissionId(pid);
			rolePermissionService.save(rolep);
		}
		return Result.success();
	}

}

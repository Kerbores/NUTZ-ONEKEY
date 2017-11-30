package club.zhcs.thunder.biz.acl;

import java.util.Collections;
import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.acl.UserPermission;
import club.zhcs.thunder.bean.acl.UserRole;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@Service
public class UserService extends BaseService<User> {

	@Autowired
	UserPermissionService userPermissionService;

	@Autowired
	UserRoleService userRoleService;

	/**
	 * @param id
	 * @param old
	 * @param newPwd
	 * @return
	 */
	public Result changePassword(int id, String old, String newPwd) {
		User user = fetch(id);
		if (user == null) {
			return Result.fail("用户不存在!");
		}
		if (!Strings.equals(Lang.md5(old), user.getPassword())) {
			return Result.fail("旧密码不正确");
		}
		user.setPassword(Lang.md5(newPwd));
		return update(user) == 1 ? Result.success() : Result.fail("修改密码失败");
	}

	/**
	 * @author 王贵源
	 * @param id
	 * @return
	 */
	public List<Record> findPermissionsWithUserPowerdInfoByUserId(int id) {
		Sql sql = dao().sqls().create("find.permissions.with.user.powered.info.by.user.id");
		sql.params().set("id", id);
		return search(sql);
	}

	/**
	 * @author 王贵源
	 * @param id
	 * @return
	 */
	public List<Record> findRolesWithUserPowerdInfoByUserId(int id) {
		Sql sql = dao().sqls().create("find.roles.with.user.powerd.info.by.user.id");
		sql.params().set("id", id);
		return search(sql);
	}

	/**
	 * @author 王贵源
	 * @param ids
	 * @param id
	 * @return
	 */
	public Result setPermission(long[] ids, long userId) {
		/**
		 * 1.查询用户现在的全部权限<br>
		 * 2.遍历权限,如果存在更新时间,如果不存在删除,处理之后从目标数组中移除元素<br>
		 * 3.遍历剩余的目标数组,添加关系
		 */
		if (ids == null) {
			ids = new long[] {};
		}
		List<Long> newIds = Lang.array2list(ids, Long.class);
		Collections.sort(newIds);
		List<UserPermission> list = userPermissionService.query(Cnd.where("userId", "=", userId));
		for (UserPermission user : list) {
			int i = 0;
			if ((i = Collections.binarySearch(newIds, user.getPermissionId())) >= 0) {
				newIds.remove(i);
			} else {
				userPermissionService.delete(user.getId());
			}
		}
		for (long pid : newIds) {
			UserPermission userp = new UserPermission();
			userp.setUserId(userId);
			userp.setPermissionId(pid);
			userPermissionService.save(userp);
		}
		return Result.success();
	}

	/**
	 * @author 王贵源
	 * @param ids
	 * @param id
	 * @return
	 */
	public Result setRole(long[] ids, long userId) {
		/**
		 * 1.查询用户现在的全部角色<br>
		 * 2.遍历角色,如果存在更新时间,如果不存在删除,处理之后从目标数组中移除元素<br>
		 * 3.遍历剩余的目标数组,添加关系
		 */
		if (ids == null) {
			ids = new long[] {};
		}
		List<Long> newIds = Lang.array2list(ids, Long.class);
		Collections.sort(newIds);
		List<UserRole> userRoles = userRoleService.query(Cnd.where("userId", "=", userId));
		for (UserRole role : userRoles) {
			int i = 0;
			if ((i = Collections.binarySearch(newIds, role.getRoleId())) >= 0) {
				newIds.remove(i);
			} else {
				userRoleService.delete(role.getId());
			}
		}
		for (long rid : newIds) {
			UserRole relation = new UserRole();
			relation.setRoleId(rid);
			relation.setUserId(userId);
			userRoleService.save(relation);
		}
		return Result.success();
	}

}

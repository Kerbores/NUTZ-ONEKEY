package club.zhcs.nail.biz.shiro;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import club.zhcs.nail.bean.acl.Permission;
import club.zhcs.nail.bean.acl.Role;
import club.zhcs.nail.bean.acl.User;
import club.zhcs.nail.bean.acl.User.Status;
import club.zhcs.nail.bean.log.LoginLog;
import club.zhcs.nail.biz.acl.PermissionService;
import club.zhcs.nail.biz.acl.RoleService;
import club.zhcs.nail.biz.acl.UserService;
import club.zhcs.nail.biz.log.LoginLogService;
import club.zhcs.titans.utils.db.Result;

/**
 * 
 * @author guiyuan.wang-N
 *
 */
@Service
public class ShiroUserService {
	Log log = Logs.get();
	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@Autowired
	LoginLogService loginLogService;

	@Autowired
	PropertiesProxy config;

	/**
	 * 检查权限
	 * 
	 * @param permission
	 *            权限名称
	 * @param id
	 *            用户 id
	 * @return 用户是否有参数权限的标识
	 *
	 * @author 王贵源
	 */
	public boolean checkPermission(String permission, int id) {

		for (String p : getAllPermissionsInfo(id)) {
			if (Strings.equals(p, permission)) {
				return true;
			}
		}
		return false;
	}

	public List<Role> roles(String userName) {
		User user = userService.fetch(userName);
		if (user == null) {
			return Lists.newArrayList();
		}
		return getAllRoles(user.getId());
	}

	public List<Permission> permissions(String userName) {
		User user = userService.fetch(userName);
		if (user == null) {
			return Lists.newArrayList();
		}
		return getAllPermissions(user.getId());
	}

	public List<String> roleInfos(String userName) {
		final List<String> target = Lists.newArrayList();
		Lang.each(roles(userName), new Each<Role>() {

			@Override
			public void invoke(int arg0, Role ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				target.add(ele.getName());
			}
		});
		return target;
	}

	public List<String> permissionInfos(String userName) {
		final List<String> target = Lists.newArrayList();
		Lang.each(permissions(userName), new Each<Permission>() {

			@Override
			public void invoke(int arg0, Permission ele, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				target.add(ele.getName());
			}
		});
		return target;
	}

	/**
	 * 检查角色
	 * 
	 * @param role
	 *            角色名称
	 * @param id
	 *            用户 id
	 * @return 用户是否有参数角色的标识
	 *
	 * @author 王贵源
	 */
	public boolean checkRole(String role, int id) {
		for (String r : getRolesInfo(id)) {
			if (Strings.equals(role, r)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据用户名查询用户
	 * 
	 * @param userName
	 *            用户名
	 * @return 用户
	 *
	 * @author 王贵源
	 */
	public User findByName(String userName) {
		return userService.fetch(userName);
	}

	/**
	 * 查询用户的全部权限
	 * 
	 * @param id
	 *            用户 id
	 * @return 权限列表
	 *
	 * @author 王贵源
	 */
	public List<Permission> getAllPermissions(long id) {
		return permissionService.getAllPermissionsByUserId(id);
	}

	/**
	 * 根据用户获取权限信息
	 * 
	 * @param id
	 *            用户 id
	 * @return 权限名称列表
	 *
	 * @author 王贵源
	 */
	public List<String> getAllPermissionsInfo(int id) {
		List<Permission> permissions = getAllPermissions(id);
		final List<String> target = Lists.newArrayList();
		Lang.each(permissions, new Each<Permission>() {

			@Override
			public void invoke(int index, Permission ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				target.add(ele.getName());
			}
		});
		return target;
	}

	/**
	 * 获取用户全部角色
	 * 
	 * @param id
	 *            用户 id
	 * @return 角色列表
	 *
	 * @author 王贵源
	 */
	public List<Role> getAllRoles(long id) {
		return getDirectRoles(id);
	}

	/**
	 * 获取用户直接角色
	 * 
	 * @param id
	 *            用户 id
	 * @return 角色列表
	 *
	 * @author 王贵源
	 */
	public List<Role> getDirectRoles(long id) {
		return roleService.listByUserId(id);
	}

	/**
	 * 获取用户间接角色
	 * 
	 * @param id
	 *            用户 id
	 * @param type
	 *            用户类型
	 * @return 角色列表
	 *
	 * @author 王贵源
	 */
	public List<Role> getIndirectRoles(int id) {
		return Lists.newArrayList();
	}

	/**
	 * 获取用户的角色信息列表
	 * 
	 * @param id
	 *            用户 id
	 * @return 角色名称列表
	 *
	 * @author 王贵源
	 */
	public List<String> getRolesInfo(int id) {
		final List<String> roles = Lists.newArrayList();
		Lang.each(getAllRoles(id), new Each<Role>() {

			@Override
			public void invoke(int index, Role ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				roles.add(ele.getName());
			}
		});
		return roles;
	}

	/**
	 * 用户登录
	 *
	 * @param userName
	 *            用户名
	 * @param p
	 *            密码
	 * @return 登录结果
	 *
	 * @author 王贵源
	 */
	public Result login(String userName, String p, String ip) {
		int times = config.getInt("login.try.times", 5);
		LoginLog loginLog = new LoginLog();
		loginLog.setAccount(userName);
		loginLog.setIp(ip);
		User user = findByName(userName);
		try {
			if (user == null) {
				loginLog.setSuccess(false);
				return Result.fail("用户名或密码不存在");
			}
			if (user.getErrorTimes() >= times) {
				user.setStatus(Status.D);
				userService.update(user);
				return Result.fail(String.format("当日登录失败次数已达到 %d 次,请明日再试,或联系系统管理员", times));
			}
			if (!user.isAvailable()) {
				loginLog.setSuccess(false);
				user.setErrorTimes(user.getErrorTimes() + 1);
				userService.update(user);
				return Result.fail("账户被锁定");
			}
			Subject currentUser = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName, p);
			token.setRememberMe(true);
			currentUser.login(token);
			loginLog.setUserId(user.getId());
			loginLog.setSuccess(true);
			user.setErrorTimes(0);
			userService.update(user);
			return Result.success().addData("loginUser", user);
		} catch (LockedAccountException e) {
			loginLog.setSuccess(false);
			user.setErrorTimes(user.getErrorTimes() + 1);
			userService.update(user);
			log.debug(e);
			return Result.fail("账户被锁定");
		} catch (Exception e) {
			loginLog.setSuccess(false);
			user.setErrorTimes(user.getErrorTimes() + 1);
			userService.update(user);
			log.debug(e);
			return Result.fail("登录失败");
		} finally {
			loginLogService.save(loginLog);
		}
	}

}

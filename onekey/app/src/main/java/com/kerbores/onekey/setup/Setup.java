package com.kerbores.onekey.setup;

import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;

import com.kerbores.onekey.biz.acl.PermissionService;
import com.kerbores.onekey.biz.acl.RolePermissionService;
import com.kerbores.onekey.biz.acl.RoleService;
import com.kerbores.onekey.biz.acl.UserRoleService;
import com.kerbores.onekey.biz.acl.UserService;
import com.kerbores.onkey.bean.InstalledRole;
import com.kerbores.onkey.bean.acl.Permission;
import com.kerbores.onkey.bean.acl.Role;
import com.kerbores.onkey.bean.acl.RolePermission;
import com.kerbores.onkey.bean.acl.User;
import com.kerbores.onkey.bean.acl.User.Status;
import com.kerbores.onkey.bean.acl.UserRole;
import com.kerbores.onkey.bean.config.Config;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project app
 *
 * @file SetUp.java
 *
 * @description // TODO write some description of this class
 *
 * @time 2016年3月15日 上午9:03:42
 *
 */
public class Setup implements org.nutz.mvc.Setup {

	Dao dao;

	RoleService roleService;

	UserRoleService userRoleService;

	RolePermissionService rolePermissionService;

	PermissionService permissionService;

	Role admin;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.mvc.Setup#init(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void init(NutConfig config) {
		dao = config.getIoc().get(Dao.class);
		Ioc ioc = config.getIoc();

		ioc.get(NutQuartzCronJobFactory.class);// 触发任务
		roleService = ioc.get(RoleService.class);

		userRoleService = ioc.get(UserRoleService.class);

		permissionService = ioc.get(PermissionService.class);

		rolePermissionService = ioc.get(RolePermissionService.class);

		Daos.createTablesInPackage(dao, "com.kerbores", false);// 创建表
		Daos.migration(dao, "com.kerbores", true, true, true);// 更改表

		initACL(config.getIoc());// 初始化访问控制数据

		writeConfigs(config.getIoc());// 写配置
	}

	/**
	 * @author 王贵源
	 * @param ioc
	 */
	private void writeConfigs(Ioc ioc) {
		// 数据库值为准没有的情况下写默认值
		final PropertiesProxy config = ioc.get(PropertiesProxy.class, "config");
		Dao dao = ioc.get(Dao.class);
		List<Config> installedConfigs = dao.query(Config.class, null);
		Lang.each(installedConfigs, new Each<Config>() {

			@Override
			public void invoke(int index, Config conf, int length) throws ExitLoop, ContinueLoop, LoopException {
				config.set(conf.getName(), conf.getValue());
			}
		});
		// 报警相关
	}

	/**
	 * @param ioc
	 */
	private void initACL(Ioc ioc) {

		UserService userService = ioc.get(UserService.class);
		User surperMan = null;
		if ((surperMan = userService.fetch(Cnd.where("name", "=", "admin"))) == null) {
			surperMan = new User();
			surperMan.setEmail("kerbores@zhcs.club");
			surperMan.setName("admin");
			surperMan.setPassword(Lang.md5("123456"));
			surperMan.setPhone("18996359755");
			surperMan.setRealName("王贵源");
			surperMan.setStatus(Status.ACTIVED);
			surperMan = userService.save(surperMan);
		}

		// 角色数据

		admin = roleService.fetch(Cnd.where("name", "=", InstalledRole.ADMIN.getName()));
		if (admin == null) {
			admin = roleService.save(InstalledRole.ADMIN.toRole());
		}

		// 超级管理员
		if (userRoleService.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId())) == null) {
			UserRole ur = new UserRole();
			ur.setRoleId(admin.getId());
			ur.setUserId(surperMan.getId());
			ur.setType(surperMan.getUserType());
			userRoleService.save(ur);
		}

		String info = Files.read("conf/menu.js");
		List<Map> list = (List) Json.fromJson(info);
		dao.create(Permission.class, true);
		Lang.each(list, new Each<Map>() {

			@Override
			public void invoke(int index, Map map, int length) throws ExitLoop, ContinueLoop, LoopException {
				NutMap data = NutMap.WRAP(map);
				save(data, null);
			}
		});

	}

	/**
	 * 
	 * @param data
	 * @param pName
	 *
	 * @author 王贵源
	 */
	protected void save(NutMap data, String pName) {
		if (hasSub(data)) {
			final Permission p = Lang.map2Object(data, Permission.class);
			p.setNeedPermission(pName);
			p.setInstalled(true);
			if (permissionService.fetch(Cnd.where("name", "=", p.getName())) == null) {
				dao.insert(p);
				RolePermission rp = rolePermissionService.fetch(Cnd.where("roleId", "=", admin.getId()).and("permissionId", "=", p.getId()));
				if (rp == null) {
					// 为admin授权
					rp = new RolePermission();
					rp.setPermissionId(p.getId());
					rp.setRoleId(admin.getId());
					rolePermissionService.save(rp);
				}
			}
			// 获取 sub 迭代 递归
			Lang.each(data.getList("sub", NutMap.class), new Each<NutMap>() {

				@Override
				public void invoke(int index, NutMap ele, int length) throws ExitLoop, ContinueLoop, LoopException {
					save(ele, p.getName());
				}
			});

		} else {
			Permission p = Lang.map2Object(data, Permission.class);
			p.setNeedPermission(pName);
			p.setInstalled(true);
			if (permissionService.fetch(Cnd.where("name", "=", p.getName())) == null) {
				dao.insert(p);
				RolePermission rp = rolePermissionService.fetch(Cnd.where("roleId", "=", admin.getId()).and("permissionId", "=", p.getId()));
				if (rp == null) {
					// 为admin授权
					rp = new RolePermission();
					rp.setPermissionId(p.getId());
					rp.setRoleId(admin.getId());
					rolePermissionService.save(rp);
				}
			}
		}
	}

	/**
	 * 
	 * @param data
	 * @return
	 *
	 * @author 王贵源
	 */
	protected boolean hasSub(NutMap data) {
		return data.get("sub") != null && !data.getList("sub", Object.class).isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.mvc.Setup#destroy(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void destroy(NutConfig nc) {
		Logs.get().debug("shutdown!!");
	}

}

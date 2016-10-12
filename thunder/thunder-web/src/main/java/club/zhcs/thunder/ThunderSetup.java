package club.zhcs.thunder;

import java.nio.charset.Charset;

import net.sf.ehcache.CacheManager;

import org.apache.log4j.PropertyConfigurator;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.bean.acl.RolePermission;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.acl.User.Status;
import club.zhcs.thunder.bean.acl.UserRole;
import club.zhcs.thunder.bean.config.Config;
import club.zhcs.thunder.biz.acl.PermissionService;
import club.zhcs.thunder.biz.acl.RolePermissionService;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.biz.acl.UserRoleService;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.config.ConfigService;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.thunder.vo.InstalledRole;

public class ThunderSetup implements Setup {
	private static final Log log = Logs.get();

	Role admin;

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see org.nutz.mvc.Setup#destroy(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void destroy(NutConfig nc) {

	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see org.nutz.mvc.Setup#init(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void init(NutConfig nc) {

		String logConfigPath = "/var/config/log4j.properties";
		try {
			if (Files.checkFile(logConfigPath) != null) {// 找到了线上配置
				PropertyConfigurator.configure(new PropertiesProxy(logConfigPath).toProperties());// 那么加载线上的配置吧!!!
			}
		} catch (Exception e) {
		}
		NutShiro.DefaultLoginURL = "/";
		NutShiro.DefaultNoAuthURL = "/403";

		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}

		Ioc ioc = nc.getIoc();

		Dao dao = ioc.get(Dao.class);

		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);

		ioc.get(NutQuartzCronJobFactory.class);// 触发任务

		// ioc.get(SigarClient.class);// 触发 sigar

		// 为全部标注了@Table的bean建表

		Daos.createTablesInPackage(dao, getClass().getPackage().getName() + ".bean", false);
		Daos.migration(dao, getClass().getPackage().getName() + ".bean", true, true);

		ConfigService configService = ioc.get(ConfigService.class);

		final PropertiesProxy p = ioc.get(PropertiesProxy.class, "config");

		Lang.each(configService.queryAll(), new Each<Config>() {

			@Override
			public void invoke(int arg0, Config config, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				p.put(config.getName(), config.getValue());
			}
		});

		final UserService userService = ioc.get(UserService.class);
		final RoleService roleService = ioc.get(RoleService.class);
		final PermissionService permissionService = ioc.get(PermissionService.class);
		final UserRoleService userRoleService = ioc.get(UserRoleService.class);
		final RolePermissionService rolePermissionService = ioc.get(RolePermissionService.class);

		Lang.each(InstalledRole.values(), new Each<InstalledRole>() {// 内置角色

					@Override
					public void invoke(int index, InstalledRole role, int length) throws ExitLoop, ContinueLoop, LoopException {
						if (roleService.fetch(Cnd.where("name", "=", role.getName())) == null) {
							Role temp = new Role();
							temp.setName(role.getName());
							temp.setDescription(role.getDescription());
							roleService.save(temp);
						}
					}
				});

		admin = roleService.fetch(Cnd.where("name", "=", InstalledRole.SU.getName()));

		if (admin == null) {// 这里理论上是进不来的,防止万一吧
			admin = new Role();
			admin.setName(InstalledRole.SU.getName());
			admin.setDescription(InstalledRole.SU.getDescription());
			admin = roleService.save(admin);
		}

		Lang.each(InstallPermission.values(), new Each<InstallPermission>() {// 内置权限

					@Override
					public void invoke(int index, InstallPermission permission, int length) throws ExitLoop, ContinueLoop, LoopException {
						Permission temp = null;
						if ((temp = permissionService.fetch(Cnd.where("name", "=", permission.getName()))) == null) {
							temp = new Permission();
							temp.setName(permission.getName());
							temp.setDescription(permission.getDescription());
							temp = permissionService.save(temp);
						}

						// 给SU授权
						if (rolePermissionService.fetch(Cnd.where("permissionId", "=", temp.getId()).and("roleId", "=", admin.getId())) == null) {
							RolePermission rp = new RolePermission();
							rp.setRoleId(admin.getId());
							rp.setPermissionId(temp.getId());
							rolePermissionService.save(rp);
						}
					}
				});

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

		UserRole ur = null;
		if ((ur = userRoleService.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId()))) == null) {
			ur = new UserRole();
			ur.setUserId(surperMan.getId());
			ur.setRoleId(admin.getId());
			userRoleService.save(ur);
		}
	}

}

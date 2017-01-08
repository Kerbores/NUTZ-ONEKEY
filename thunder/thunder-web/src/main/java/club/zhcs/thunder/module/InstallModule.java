package club.zhcs.thunder.module;

import java.io.File;

import org.nutz.dao.Cnd;
import org.nutz.dao.DB;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.SimpleDataSource;
import org.nutz.dao.util.DaoUp;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.weixin.impl.WxApi2Impl;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;

import club.zhcs.thunder.ThunderSetup;
import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.bean.acl.RolePermission;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.acl.User.Status;
import club.zhcs.thunder.bean.acl.UserRole;
import club.zhcs.thunder.bean.config.Config;
import club.zhcs.thunder.bean.config.WxConfig;
import club.zhcs.thunder.biz.acl.PermissionService;
import club.zhcs.thunder.biz.acl.RolePermissionService;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.biz.acl.UserRoleService;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.config.ConfigService;
import club.zhcs.thunder.biz.config.WxConfigService;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.thunder.vo.InstalledRole;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Result;

public class InstallModule extends AbstractBaseModule {

	@Inject
	PropertiesProxy config;

	@Inject
	Dao dao;

	@Inject
	DruidDataSource dataSource;

	Role admin;

	@Inject
	ConfigService configService;

	@Inject
	WxApi2Impl wxApi;

	@Inject
	WxConfigService wxConfigService;

	@Inject
	UserService userService;

	@Inject
	RoleService roleService;

	@Inject
	PermissionService permissionService;

	@Inject
	UserRoleService userRoleService;

	@Inject
	RolePermissionService rolePermissionService;

	@At
	@GET
	@Filters
	@Ok("beetl:pages/install/install.html")
	public Result install() {
		return Result.success().setTitle("安装");
	}

	public static void main(String[] args) {
		SimpleDataSource ds = new SimpleDataSource();
		ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/thunder?useUnicode=true&characterEncoding=utf8&useSSL=false");
		ds.setPassword("123456");
		ds.setUsername("root");
		DaoUp up = DaoUp.me();
		up.setDataSource(ds);
		System.err.println(Json.toJson(up.dao().meta()));
	}

	@At
	@POST
	@Filters
	public Result install(DB db, String jdbc, String user, String password) throws Exception {

		// 首先得保证参入的参数是可以连接的
//		try {
//			SimpleDataSource ds = new SimpleDataSource();
//			Lang.loadClass("com.mysql.jdbc.Driver");
//			ds.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/thunder?useUnicode=true&characterEncoding=utf8&useSSL=false");
//			ds.setPassword("123456");
//			ds.setUsername("root");
//			DaoUp up = DaoUp.me();
//			up.setDataSource(ds);
//			up.dao().meta();
//		} catch (Exception e) {
//			return Result.fail("无法连接的数据源");
//		}

		String[] arr = ConfigTools.genKeyPair(512);
		String publicKey = arr[1];
		String encodedPwd = ConfigTools.encrypt(arr[0], password);

		if (db == DB.H2 && jdbc.indexOf("MODE") < 0) {
			jdbc += ";MODE=MYSQL";
		}

		String config = "db-driver=" + (db == DB.H2 ? "org.h2.Driver" : "com.mysql.jdbc.Driver") + "\r\n";
		config += "db-url=" + jdbc + "\r\n";
		config += "db-user=" + user + "\r\n";
		config += "db-pwd=" + encodedPwd + "\r\n";
		config += "connectionProperties="
				+ String.format("config.decrypt=true;config.decrypt.key=%s", publicKey + "\r\n");

		try {
			File file = Files.createFileIfNoExists(System.getProperty("user.home") + "/config/datasource.properties");
			Files.write(file, config);
		} catch (Exception e) {
			return Result.fail("写入配置文件失败!");
		}
		return Result.success();
	}

	@At
	@POST
	@Filters
	public Result finish() {
		try {
			File file = Files.createFileIfNoExists(System.getProperty("user.home") + "/config/more.properties");
			config.put("install-flag", "true");
			Files.write(file, "install-flag=true");
		} catch (Exception e) {
			return Result.fail("写入配置文件失败!");
		}
		return Result.success();
	}

	@At("/switch")
	@POST
	@Filters
	public Result switchDataSource() throws CloneNotSupportedException {
		PropertiesProxy config = new PropertiesProxy(false);
		dataSource = (DruidDataSource) dataSource.clone();
		config.setIgnoreResourceNotFound(true);
		config.setPaths("datasource", "~/config/datasource.properties");
		dataSource.setUrl(config.get("db-url"));
		dataSource.setDriverClassName(config.get("db-driver"));
		dataSource.setUsername(config.get("db-user"));
		dataSource.setPassword(config.get("db-pwd"));
		dataSource.setConnectionProperties(config.get("connectionProperties"));
		dao = new NutDao(dataSource);

		try {
			dao.meta();
			return Result.success();
		} catch (Exception e) {
			e.printStackTrace();
			return Result.fail("数据源无法连接!");
		}
	}

	@At
	@POST
	@Filters
	public Result init() {
		Daos.createTablesInPackage(dao, ThunderSetup.class.getPackage().getName() + ".bean", false);
		Daos.migration(dao, ThunderSetup.class.getPackage().getName() + ".bean", true, true);

		WxConfig wxConfig = wxConfigService.fetch(Cnd.orderBy().desc("id"));
		if (wxConfig != null) {
			wxApi.setAppid(wxConfig.getAppid());
			wxApi.setAppsecret(wxConfig.getAppsecret());
			wxApi.setEncodingAesKey(wxConfig.getEncodingAesKey());
			wxApi.setToken(wxConfig.getToken());
		}

		Lang.each(configService.queryAll(), new Each<Config>() {

			@Override
			public void invoke(int arg0, Config config, int arg2) throws ExitLoop, ContinueLoop, LoopException {
				InstallModule.this.config.put(config.getName(), config.getValue());
			}
		});

		Lang.each(InstalledRole.values(), new Each<InstalledRole>() {// 内置角色

			@Override
			public void invoke(int index, InstalledRole role, int length) throws ExitLoop, ContinueLoop, LoopException {
				if (roleService.fetch(Cnd.where("name", "=", role.getName())) == null) {
					Role temp = new Role();
					temp.setInstalled(true);
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
			public void invoke(int index, InstallPermission permission, int length)
					throws ExitLoop, ContinueLoop, LoopException {
				Permission temp = null;
				if ((temp = permissionService.fetch(Cnd.where("name", "=", permission.getName()))) == null) {
					temp = new Permission();
					temp.setName(permission.getName());
					temp.setDescription(permission.getDescription());
					temp = permissionService.save(temp);
				}

				// 给SU授权
				if (rolePermissionService.fetch(
						Cnd.where("permissionId", "=", temp.getId()).and("roleId", "=", admin.getId())) == null) {
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
		if ((ur = userRoleService
				.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId()))) == null) {
			ur = new UserRole();
			ur.setUserId(surperMan.getId());
			ur.setRoleId(admin.getId());
			userRoleService.save(ur);
		}
		return Result.success();
	}

}

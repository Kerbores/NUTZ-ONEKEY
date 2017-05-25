package club.zhcs.nail;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import club.zhcs.nail.bean.acl.Permission;
import club.zhcs.nail.bean.acl.Role;
import club.zhcs.nail.bean.acl.RolePermission;
import club.zhcs.nail.bean.acl.User;
import club.zhcs.nail.bean.acl.User.Status;
import club.zhcs.nail.bean.acl.UserRole;
import club.zhcs.nail.bean.config.Config;
import club.zhcs.nail.biz.acl.PermissionService;
import club.zhcs.nail.biz.acl.RolePermissionService;
import club.zhcs.nail.biz.acl.RoleService;
import club.zhcs.nail.biz.acl.UserRoleService;
import club.zhcs.nail.biz.acl.UserService;
import club.zhcs.nail.biz.config.ConfigService;
import club.zhcs.nail.shiro.InstalledPermission;
import club.zhcs.nail.shiro.InstalledRole;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableRedisHttpSession
public class NailApplicatin extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(NailApplicatin.class);
		application.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			Role admin;

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				ApplicationContext context = event.getApplicationContext();
				if (context.getParent() == null) {// 保险一点儿
					Dao dao = context.getBean(Dao.class);
					Daos.createTablesInPackage(dao, "club.zhcs", false);// 初始化一下
					Daos.migration(dao, "club.zhcs", true, true);
					initAcl(context);
				}
			}

			private void initAcl(ApplicationContext context) {
				// log.debug("init acl...");
				final UserService userService = context.getBean(UserService.class);
				final RoleService roleService = context.getBean(RoleService.class);
				final PermissionService permissionService = context.getBean(PermissionService.class);
				final UserRoleService userRoleService = context.getBean(UserRoleService.class);
				final RolePermissionService rolePermissionService = context.getBean(RolePermissionService.class);

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

				Lang.each(InstalledPermission.values(), new Each<InstalledPermission>() {// 内置权限

					@Override
					public void invoke(int index, InstalledPermission permission, int length) throws ExitLoop, ContinueLoop, LoopException {
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
					surperMan.setRealName("Kerbores");
					surperMan.setNickName("Kerbores");
					surperMan.setStatus(Status.A);
					surperMan = userService.save(surperMan);
				}

				UserRole ur = null;
				if ((ur = userRoleService.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId()))) == null) {
					ur = new UserRole();
					ur.setUserId(surperMan.getId());
					ur.setRoleId(admin.getId());
					userRoleService.save(ur);
				}

				ConfigService configService = context.getBean(ConfigService.class);
				PropertiesProxy p = context.getBean(PropertiesProxy.class);

				Lang.each(p.toMap().keySet(), new Each<String>() {

					@Override
					public void invoke(int index, String key, int length) throws ExitLoop, ContinueLoop, LoopException {
						if (configService.fetch(Cnd.where("name", "=", key)) == null) {// 没有配置
							Config config = new Config();
							config.setName(key);
							config.setValue(p.get(key));
							config.setInstalled(true);
							configService.save(config);
						}
					}
				});

				// 加载
				Lang.each(configService.queryAll(), new Each<Config>() {

					@Override
					public void invoke(int arg0, Config config, int arg2) throws ExitLoop, ContinueLoop, LoopException {
						p.put(config.getName(), config.getValue());
					}
				});
			}
		});
		application.run(args);
	}

	@Bean
	public PropertiesProxy config() {
		PropertiesProxy config = new PropertiesProxy();
		config.setIgnoreResourceNotFound(true);
		config.setPaths("conf", System.getProperty("nail.config"), System.getProperty("nail.common.config"));
		return config;
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(NailApplicatin.class);
	}

}

package club.zhcs.thunder;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.bean.acl.RolePermission;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.acl.User.Status;
import club.zhcs.thunder.bean.acl.UserRole;
import club.zhcs.thunder.biz.acl.PermissionService;
import club.zhcs.thunder.biz.acl.RolePermissionService;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.biz.acl.UserRoleService;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.ext.shiro.matcher.SINOCredentialsMatcher;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.thunder.vo.InstalledRole;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@SpringBootApplication
@EnableRedisHttpSession
@EnableAsync
@EnableTransactionManagement
public class BootNutzVueApplication extends WebMvcConfigurerAdapter {

	public static final String CAPTCHA_KEY = "SINO_CAPTCHA";
	public static final String USER_KEY = "SINO_USER_KEY";
	public static final String USER_COOKIE_KEY = "SINO_USER_COOKIE";
	public static final String NUTZ_USER_KEY = "SINO_NUTZ_USER_KEY";

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(BootNutzVueApplication.class);
		application.addListeners(new ApplicationListener<ContextRefreshedEvent>() {
			Logger log = Logger.getLogger(getClass());

			Role admin;

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				// 这里的逻辑将在应用启动之后执行
				ApplicationContext context = event.getApplicationContext();
				Dao dao = context.getBean(Dao.class);
				if (context.getParent() == null) {
					log.debug("application starter...");
					Daos.createTablesInPackage(dao, "club.zhcs.thunder.bean", false);// 确保表结构正确
					Daos.migration(dao, "club.zhcs.thunder.bean", true, true);
					initAcl(context);
				}
			}

			private void initAcl(ApplicationContext context) {
				log.debug("init acl...");
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

				Lang.each(InstallPermission.values(), new Each<InstallPermission>() {// 内置权限

					@Override
					public void invoke(int index, InstallPermission permission, int length) throws ExitLoop, ContinueLoop, LoopException {
						Permission temp = null;
						if ((temp = permissionService.fetch(Cnd.where("name", "=", permission.getName()))) == null) {
							temp = new Permission();
							temp.setName(permission.getName());
							temp.setDescription(permission.getDescription());
							temp.setInstalled(true);
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
					surperMan.setPassword(SINOCredentialsMatcher.password("admin", "12345678"));
					surperMan.setPhone("18996359755");
					surperMan.setRealName("Kerbores");
					surperMan.setNickName("Kerbores");
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

		});
		application.run(args);
	}

}

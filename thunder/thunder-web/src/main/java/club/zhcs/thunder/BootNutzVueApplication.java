package club.zhcs.thunder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.http.Http;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.impl.WxApi2Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import club.zhcs.thunder.bean.acl.Permission;
import club.zhcs.thunder.bean.acl.Role;
import club.zhcs.thunder.bean.acl.RolePermission;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.acl.User.Status;
import club.zhcs.thunder.bean.acl.UserRole;
import club.zhcs.thunder.bean.qa.Nutzer;
import club.zhcs.thunder.biz.acl.PermissionService;
import club.zhcs.thunder.biz.acl.RolePermissionService;
import club.zhcs.thunder.biz.acl.RoleService;
import club.zhcs.thunder.biz.acl.UserRoleService;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.qa.NutzerService;
import club.zhcs.thunder.ext.shiro.matcher.SINOCredentialsMatcher;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.thunder.vo.InstalledRole;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableRedisHttpSession
@EnableSwagger2
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
					Daos.createTablesInPackage(dao, "com.sino.scaffold.bean", false);// 确保表结构正确
					Daos.migration(dao, "com.sino.scaffold.bean", true, true);
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
					surperMan.setPassword(SINOCredentialsMatcher.password("admin", "123456"));
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

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false)
				.forCodeGeneration(true)
				.pathMapping("/")
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.sino"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("F")
				.description("接口手册")// 详细描述
				.version("1.0")// 版本
				.termsOfServiceUrl("http://www.sinosoft.com.cn")
				.contact(new Contact("王贵源", "http://www.sinosoft.com.cn", "wangguiyuan@sinosoft.com.cn"))// 作者
				.license("The Apache License, Version 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
				.build();
	}

	public static class QAUserCheckInterceptor implements HandlerInterceptor {

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			HandlerMethod method = (HandlerMethod) handler;
			if (!bindedUser(request) && needSession(method)) {
				response.setStatus(401);
				return false;
			}
			return true;
		}

		private boolean needSession(HandlerMethod method) {
			for (MethodParameter parameter : method.getMethodParameters()) {
				if (parameter.getParameterAnnotation(SessionAttribute.class) != null) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 已绑定用户
		 * 
		 * @param request
		 * @return
		 */
		private boolean bindedUser(HttpServletRequest request) {
			if (request.getSession().getAttribute(BootNutzVueApplication.NUTZ_USER_KEY) == null) {
				return false;
			} else if (!(request.getSession().getAttribute(BootNutzVueApplication.NUTZ_USER_KEY) instanceof Nutzer)) {
				return false;
			} else if (Strings.isBlank(((Nutzer) request.getSession().getAttribute(BootNutzVueApplication.NUTZ_USER_KEY)).getOpenid())) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

		}
	}

	public static class QAUserInjectInterceptor implements HandlerInterceptor {

		@Autowired
		WxApi2Impl api;

		@Autowired
		NutzerService nutzerService;

		Log log = Logs.get();

		public boolean isDebug(HttpServletRequest request) {
			String serverName = request.getServerName();
			return Strings.equalsIgnoreCase("kerbores.ngrok.wendal.cn", serverName)
					|| Strings.equalsIgnoreCase("127.0.0.1", serverName)
					|| Strings.equalsIgnoreCase("localhost", serverName);
		}

		@Override
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			if (isDebug(request)) {
				request.getSession().setAttribute("openid", "otOKDvwEbkaeI8MewpbAFWonrCp0");
				request.getSession().setAttribute(BootNutzVueApplication.NUTZ_USER_KEY, nutzerService.fetch(Cnd.where("openid", "=", "otOKDvwEbkaeI8MewpbAFWonrCp0")));
				// 继续尝试是否可真实获取
			}
			String code = request.getParameter("code");
			if (Strings.isBlank(code)) {// 没有code参数
				return true;
			}
			String wechatInterface = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + api.getAppid() + "&secret=" + api.getAppsecret() + "&code=" + code
					+ "&grant_type=authorization_code";
			log.debug("ready to invoke url : " + wechatInterface);
			String info = Http.get(wechatInterface).getContent();
			NutMap data = Lang.map(info);
			if (data.get("errcode") != null) {// 调用微信出错
				log.error("=====error msg:" + data.get("errcode") + ",error msg:" + data.get("errmsg") + "======");
				return true;
			}
			log.debug("successful invoke ,return message:\n" + data.toString());
			request.getSession().setAttribute("openid", data.getString("openid"));
			request.getSession().setAttribute(BootNutzVueApplication.NUTZ_USER_KEY, nutzerService.fetch(Cnd.where("openid", "=", data.getString("openid"))));
			return true;
		}

		@Override
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		}

		@Override
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		}
	}

	@Bean
	public QAUserCheckInterceptor qaUserCheckInterceptor() {
		return new QAUserCheckInterceptor();
	}

	@Bean
	public QAUserInjectInterceptor qaUserInjectInterceptor() {
		return new QAUserInjectInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(qaUserInjectInterceptor()).addPathPatterns("/qa/**");// qa用户注入
		registry.addInterceptor(qaUserCheckInterceptor()).addPathPatterns("/qa/**");// qa用户检测
	}

}

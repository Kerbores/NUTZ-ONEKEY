package club.zhcs.nail.controller.acl;

import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.filter.CrossOriginFilter;
import org.nutz.plugin.spring.boot.service.entity.PageredData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import club.zhcs.nail.ThunderApplication.SessionKeys;
import club.zhcs.nail.bean.acl.User;
import club.zhcs.nail.biz.acl.PermissionService;
import club.zhcs.nail.biz.acl.RoleService;
import club.zhcs.nail.biz.acl.UserService;
import club.zhcs.nail.biz.shiro.ShiroUserService;
import club.zhcs.nail.controller.base.BaseController;
import club.zhcs.nail.rest.ApiRequest;
import club.zhcs.nail.rest.dto.acl.UserLoginDto;
import club.zhcs.nail.shiro.InstalledPermission;
import club.zhcs.nail.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.titans.nutz.captcha.JPEGView;
import club.zhcs.titans.utils.codec.DES;
import club.zhcs.titans.utils.db.Result;
import io.swagger.annotations.Api;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@RestController
@RequestMapping("/user")
@Api(value = "User", tags = "用户相关接口")
public class UserController extends BaseController {

	@Autowired
	ShiroUserService shiroUserService;

	@Autowired
	UserService userService;

	@Autowired
	RoleService roleService;

	@Autowired
	PermissionService permissionService;

	@GetMapping("list")
	@ThunderRequiresPermissions(InstalledPermission.USER_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", userService.searchByPage(_fixPage(page)));
	}

	@GetMapping("search")
	@ThunderRequiresPermissions(InstalledPermission.USER_LIST)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		PageredData<User> pager = userService.searchByKeyAndPage(key, page, "name", "nickName", "realName");
		pager.addParam("key", key);
		return Result.success().addData("pager", pager);
	}

	@PostMapping("save")
	@ThunderRequiresPermissions(InstalledPermission.USER_ADD)
	public Result save(@RequestBody User user) {
		user.setPassword(new Md5Hash(user.getPassword(), user.getName(), 2).toString());
		return userService.save(user) == null ? Result.fail("保存用户失败!") : Result.success().addData("user", user);
	}

	@PostMapping("resetPassword")
	@ThunderRequiresPermissions(InstalledPermission.USER_EDIT)
	public Result resetPassword(@RequestBody User user) {
		user.setPassword(new Md5Hash(user.getPassword(), user.getName(), 2).toString());
		return userService.update(user, "password") != 1 ? Result.fail("保存用户失败!") : Result.success().addData("user", user);
	}

	@GetMapping("detail/{id}")
	@ThunderRequiresPermissions(value = { InstalledPermission.USER_DETAIL, InstalledPermission.USER_EDIT }, logical = Logical.OR)
	public Result detail(@PathVariable("id") long id) {
		return Result.success().addData("user", userService.fetch(id));
	}

	@GetMapping("delete/{id}")
	@ThunderRequiresPermissions(InstalledPermission.USER_DELETE)
	public Result delete(@PathVariable("id") long id) {
		return userService.delete(id) == 1 ? Result.success() : Result.fail("删除用户失败!");
	}

	@GetMapping("role/{id}")
	@ThunderRequiresPermissions(InstalledPermission.USER_ROLE)
	public Result roleInfo(@PathVariable("id") long id) {
		return Result.success().addData("infos", userService.findRolesWithUserPowerdInfoByUserId(id));
	}

	@GetMapping("permission/{id}")
	@ThunderRequiresPermissions(InstalledPermission.USER_GRANT)
	public Result permissionInfo(@PathVariable("id") long id) {
		return Result.success().addData("infos", userService.findPermissionsWithUserPowerdInfoByUserId(id));
	}

	public static class GrantDTO {
		private long userId;

		private long[] grantIds;

		public long getUserId() {
			return userId;
		}

		public void setUserId(long userId) {
			this.userId = userId;
		}

		public long[] getGrantIds() {
			return grantIds;
		}

		public void setGrantIds(long[] grantIds) {
			this.grantIds = grantIds;
		}

	}

	@PostMapping("grant/role")
	@ThunderRequiresPermissions(InstalledPermission.USER_ROLE)
	public Result grantRole(@RequestBody GrantDTO dto) {
		return userService.setRole(dto.getGrantIds(), dto.getUserId());
	}

	@PostMapping("grant/permission")
	@ThunderRequiresPermissions(InstalledPermission.USER_GRANT)
	public Result grantPermission(@RequestBody GrantDTO dto) {
		return userService.setPermission(dto.getGrantIds(), dto.getUserId());
	}

	@PostMapping("update")
	@ThunderRequiresPermissions(InstalledPermission.USER_EDIT)
	public Result update(@RequestBody User user) {
		user.setPassword(null);// 不更新密码
		return userService.updateIgnoreNull(user) != 1 ? Result.fail("更新用户失败!") : Result.success().addData("user", user);
	}

	/**
	 * 登录
	 * 
	 * @param request
	 *            登录请求对象
	 * @param session
	 *            httpSession
	 * @return 登录结果
	 */
	@PostMapping("login")
	@Filters(@By(type = CrossOriginFilter.class))
	public Result login(@RequestBody ApiRequest<UserLoginDto> request, HttpSession session) {
		if (Strings.equalsIgnoreCase(request.getData().getCaptcha(), Strings.safeToString(session.getAttribute(JPEGView.CAPTCHA), ""))) {
			Result result = shiroUserService.login(request.getData().getUserName(), request.getData().getPassword(), Lang.getIP(Mvcs.getReq()));
			if (result.isSuccess()) {
				// 登录成功处理
				_putSession(SessionKeys.USER_KEY, result.getData().get("loginUser"));
				if (request.getData().isRememberMe()) {
					NutMap data = NutMap.NEW();
					data.put("user", request.getData().getUserName());
					data.put("password", request.getData().getPassword());
					data.put("rememberMe", request.getData().getPassword());
					_addCookie("kerbores", DES.encrypt(Json.toJson(data)), 24 * 60 * 60 * 365);
				}
			}
			return result.addData("roles", shiroUserService.roleInfos(request.getData().getUserName())).addData("permissions",
					shiroUserService.permissionInfos(request.getData().getUserName()));
		} else {
			return Result.fail("验证码输入错误");
		}
	}

	/**
	 * 退出登录
	 * 
	 * @param session
	 * @return
	 */
	@GetMapping("logout")
	@RequiresUser
	public Result logout() {
		SecurityUtils.getSubject().logout();
		return Result.success();
	}

}

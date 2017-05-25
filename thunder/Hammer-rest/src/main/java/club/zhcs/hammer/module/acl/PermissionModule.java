package club.zhcs.hammer.module.acl;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.acl.Permission;
import club.zhcs.hammer.biz.acl.PermissionService;
import club.zhcs.hammer.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.hammer.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * @author kerbores@gmail.com
 *
 */
@At("/permission")
@Api(name = "Permission", description = "权限相关接口")
public class PermissionModule extends AbstractBaseModule {

	@Inject
	PermissionService permissionService;

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", permissionService.searchByPage(_fixPage(page)));
	}

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_LIST)
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<Permission> pager = permissionService.searchByKeyAndPage(key, page, "name", "description");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_ADD)
	public Result save(Permission Permission) {
		return permissionService.save(Permission) == null ? Result.fail("保存权限失败!") : Result.success().addData("permission", Permission);
	}

	@At("/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_EDIT)
	public Result detail(long id) {
		return Result.success().addData("permission", permissionService.fetch(id));
	}

	@At("/delete/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_DELETE)
	public Result delete(long id) {
		return permissionService.delete(id) == 1 ? Result.success() : Result.fail("删除权限失败!");
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_EDIT)
	public Result update(Permission Permission) {
		return permissionService.updateIgnoreNull(Permission) != 1 ? Result.fail("更新权限失败!") : Result.success().addData("permission", Permission);
	}

}

package club.zhcs.thunder.module.codebook;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import club.zhcs.thunder.bean.config.CodeBook;
import club.zhcs.thunder.biz.codebook.CodeBookService;
import club.zhcs.thunder.biz.codebook.GroupService;
import club.zhcs.thunder.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

@At("codebook")
public class CodeBookModule extends AbstractBaseModule {

	@Inject
	CodeBookService codeBookService;

	@Inject
	GroupService groupService;

	@At
	@Ok("beetl:pages/codebook/list.html")
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<CodeBook> pager = codeBookService.searchByPage(page);
		pager.setUrl(_base() + "/codebook/list");
		return Result.success().addData("pager", pager).addData("groups", groupService.queryAll());
	}

	// @At
	// @Ok("beetl:pages/codebook/group/list.html")
	// @ThunderRequiresPermissions(InstallPermission.GROUP_LIST)
	// public Result search(@Param("key") String key, @Param(value = "page", df
	// = "1") int page) {
	// page = _fixPage(page);
	// key = _fixSearchKey(key);
	// Pager<CodeBook> pager = codeBookService.searchByKeyAndPage(key, page,
	// "name", "description");
	// pager.setUrl(_base() + "/user/search");
	// pager.addParas("key", key);
	// return Result.success().addData("pager", pager);
	// }
	//
	// @At
	// @GET
	// @Ok("beetl:pages/codebook/group/add_edit.html")
	// @ThunderRequiresPermissions(InstallPermission.GROUP_ADD)
	// public Result add() {
	// return Result.success();
	// }
	//
	// @At("/edit/*")
	// @GET
	// @Ok("beetl:pages/codebook/group/add_edit.html")
	// @ThunderRequiresPermissions(InstallPermission.GROUP_EDIT)
	// public Result edit(int id) {
	// return Result.success().addData("group", codeBookService.fetch(id));
	// }
	//
	// @At
	// @POST
	// @ThunderRequiresPermissions(InstallPermission.GROUP_ADD)
	// public Result add(@Param("..") CodeBook group) {
	// return codeBookService.save(group) != null ?
	// Result.success().addData("group", group) : Result.fail("添加分组失败!");
	// }
	//
	// @At
	// @POST
	// @ThunderRequiresPermissions(InstallPermission.GROUP_EDIT)
	// public Result edit(@Param("..") Group group) {
	// return codeBookService.update(group, "name", "description") ?
	// Result.success() : Result.fail("更新失败!");
	// }
	//
	// @At("/delete/*")
	// @ThunderRequiresPermissions(InstallPermission.GROUP_DELETE)
	// public Result delete(int id) {
	// return codeBookService.delete(id) == 1 ? Result.success() :
	// Result.fail("删除分组失败!");
	// }

}

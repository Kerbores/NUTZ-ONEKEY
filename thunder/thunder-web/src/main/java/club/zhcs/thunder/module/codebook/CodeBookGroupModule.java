package club.zhcs.thunder.module.codebook;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import club.zhcs.thunder.bean.config.Group;
import club.zhcs.thunder.biz.codebook.GroupService;
import club.zhcs.thunder.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

@At("group")
public class CodeBookGroupModule extends AbstractBaseModule {

	@Inject
	GroupService groupService;

	@At
	@Ok("beetl:pages/codebook/group/list.html")
	@ThunderRequiresPermissions(InstallPermission.GROUP_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<Group> pager = groupService.searchByPage(page);
		pager.setUrl(_base() + "/group/list");
		return Result.success().addData("pager", pager);
	}

	@At
	@GET
	@Ok("beetl:pages/codebook/group/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.GROUP_ADD)
	public Result add() {
		return Result.success();
	}

	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.GROUP_ADD)
	public Result add(@Param("..") Group group) {
		return groupService.save(group) != null ? Result.success().addData("group", group) : Result.fail("添加分组失败!");
	}

}

package club.zhcs.hammer.module.struts;

import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.struts.Branch;
import club.zhcs.hammer.biz.struts.BranchService;
import club.zhcs.hammer.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.hammer.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

@At("branch")
@Api(name = "Branch", description = "机构接口")
public class BranchModule extends AbstractBaseModule {

	@Inject
	BranchService branchService;

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.BRANCH_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", branchService.searchByPage(_fixPage(page), Cnd.where("parentId", "=", 0)));
	}

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.BRANCH_LIST)
	public Result search(@Param("key") String key, @Param(value = "group", df = "0") int groupId, @Param(value = "page", df = "1") int page) {
		Cnd cnd = groupId == 0 ? Cnd.where("parentId", "=", 0) : Cnd.where("parentId", "=", 0).and("groupId", "=", groupId);
		Pager<Branch> pager = branchService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), cnd, "name", "value");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.BRANCH_ADD)
	public Result save(Branch branch) {
		return branchService.save(branch) == null ? Result.fail("保存数据失败!") : Result.success().addData("branch", branch);
	}

	@At("/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.BRANCH_EDIT)
	public Result detail(long id) {
		return Result.success().addData("branch", branchService.fetch(id));
	}

	@At("/delete/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.BRANCH_DELETE)
	public Result delete(long id) {
		return branchService.delete(id) == 1 ? Result.success() : Result.fail("删除数据失败!");
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.BRANCH_EDIT)
	public Result update(Branch branch) {
		return branchService.updateIgnoreNull(branch) != 1 ? Result.fail("更新数据失败!") : Result.success().addData("branch", branch);
	}

	@At("/top/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.BRANCH_EDIT)
	public Result top() {
		return Result.success().addData("tops", branchService.query(Cnd.where("parentId", "=", 0)));
	}

	@At("/sub/?")
	@GET
	@ThunderRequiresPermissions(value = { InstallPermission.BRANCH_EDIT, InstallPermission.BRANCH_LIST }, logical = Logical.OR)
	public Result sub(long id) {
		return Result.success().addData("codes", branchService.query(Cnd.where("parentId", "=", id)));
	}

}

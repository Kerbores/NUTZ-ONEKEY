package club.zhcs.thunder.module.codebook;

import java.util.List;

import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
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
		Pager<CodeBook> pager = codeBookService.searchByPage(page, Cnd.where("parentId", "=", 0));
		pager.setUrl(_base() + "/codebook/list");
		return Result.success().addData("pager", pager).addData("groups", groupService.queryAll());
	}

	@At
	@Ok("beetl:pages/codebook/list.html")
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_LIST)
	public Result search(@Param("groupId") int groupId, @Param("status") int status, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Cnd cnd = groupId == 0 ? Cnd.where("parentId", "=", 0) : Cnd.where("parentId", "=", 0).and("groupId", "=", groupId);

		if (status == 1) {
			cnd = cnd.and("active", "=", true);
		} else if (status == 2) {
			cnd = cnd.and("active", "=", false);
		}

		Pager<CodeBook> pager = codeBookService.searchByPage(page, cnd);
		pager.setUrl(_base() + "/codebook/search");
		pager.addParas("groupId", groupId);
		pager.addParas("status", status);
		return Result.success().addData("pager", pager).addData("groups", groupService.queryAll());
	}

	@At
	@GET
	@Ok("beetl:pages/codebook/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_ADD)
	public Result add() {
		return Result.success().addData("groups", groupService.queryAll());
	}

	@At("/selector/*")
	@GET
	@Ok("beetl:pages/codebook/selector.html")
	@ThunderRequiresPermissions(value = { InstallPermission.CODEBOOK_ADD, InstallPermission.CODEBOOK_EDIT }, logical = Logical.OR)
	public Result selector(int groupId) {
		return Result.success().addData("data", codeBookService.query(Cnd.where("groupId", "=", groupId).and("parentId", "=", 0)));
	}

	@At
	public List<CodeBook> nodes(@Param("id") int id, @Param("status") int status) {
		Cnd cnd = Cnd.where("parentId", "=", id);
		if (status == 1) {
			cnd = cnd.and("active", "=", true);
		} else if (status == 2) {
			cnd = cnd.and("active", "=", false);
		}
		return codeBookService.query(cnd);
	}

	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_ADD)
	public Result add(@Param("..") CodeBook codeBook) {
		return codeBookService.save(codeBook) != null ? Result.success().addData("data", codeBook) : Result.fail("添加数据失败!");
	}

	@At("/edit/*")
	@GET
	@Ok("beetl:pages/codebook/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_EDIT)
	public Result edit(int id) {
		return Result.success().addData("data", codeBookService.fetch(id)).addData("groups", groupService.queryAll());
	}

	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_EDIT)
	public Result edit(@Param("..") CodeBook codeBook) {
		return codeBookService.update(codeBook, "value", "index", "parentId") ? Result.success() : Result.fail("更新失败!");
	}

	@At("/active")
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_DELETE)
	public Result active(@Param("id") int id, @Param("status") boolean status) {
		return codeBookService.update(Chain.make("active", status), Cnd.where("id", "=", id)) == 1 ? Result.success() : Result.fail((status ? "启用" : "禁用") + "数据失败!");
	}

}

package club.zhcs.thunder.module.api;

import java.util.List;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

import club.zhcs.thunder.bean.config.CodeBook;
import club.zhcs.thunder.bean.config.Group;
import club.zhcs.thunder.biz.codebook.CodeBookService;
import club.zhcs.thunder.biz.codebook.GroupService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

@Api(author = "kerbores", name = "CodeBook", description = "码本restApi", match = ApiMatchMode.ALL)
@At("/code")
@Filters
public class CodeBookApiModule extends AbstractBaseModule {

	@Inject
	GroupService groupService;

	@Inject
	CodeBookService codeBookService;

	@At("/group/list")
	public Result groupList(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<Group> pager = groupService.searchByPage(page);
		return Result.success().addData("pager", pager);
	}

	@At("/group/search")
	public Result groupSearch(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<Group> pager = groupService.searchByKeyAndPage(key, page, "name", "description");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At("/group/add")
	public Result groupAdd(@Param("..") Group group) {
		return groupService.save(group) != null ? Result.success().addData("group", group) : Result.fail("添加分组失败!");
	}

	@At("/group/edit")
	public Result groupEdit(@Param("..") Group group) {
		return groupService.update(group, "name", "description") ? Result.success() : Result.fail("更新失败!");
	}

	@At("/group/delete")
	public Result groupDelete(@Param("id") int id) {
		return groupService.delete(id) == 1 ? Result.success() : Result.fail("删除分组失败!");
	}

	@At
	public Result list(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<CodeBook> pager = codeBookService.searchByPage(page);
		return Result.success().addData("pager", pager);
	}

	@At
	public Result all() {
		return Result.success().addData("records", codeBookService.queryAll());
	}

	@At
	public Result add(@Param("..") CodeBook codeBook) {
		return codeBookService.save(codeBook) != null ? Result.success().addData("data", codeBook) : Result.fail("添加数据失败!");
	}

	@At
	public Result edit(@Param("..") CodeBook codeBook) {
		return codeBookService.update(codeBook, "value", "index", "parentId") ? Result.success() : Result.fail("更新失败!");
	}

	@At
	public Result active(@Param("id") int id, @Param("status") boolean status) {
		return codeBookService.update(Chain.make("active", status), Cnd.where("id", "=", id)) == 1 ? Result.success() : Result.fail((status ? "启用" : "禁用") + "数据失败!");
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

}

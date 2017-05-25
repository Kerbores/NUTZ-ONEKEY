package club.zhcs.hammer.module.config;

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

import club.zhcs.hammer.bean.config.CodeBook;
import club.zhcs.hammer.biz.config.CodeBookService;
import club.zhcs.hammer.ext.shiro.anno.ThunderRequiresPermissions;
import club.zhcs.hammer.vo.InstallPermission;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * @author kerbores@gmail.com
 *
 */
@At("/codebook")
@Api(name = "CodeBook", description = "码本相关接口")
public class CodeBookModule extends AbstractBaseModule {

	@Inject
	CodeBookService codebookService;

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", codebookService.searchByPage(_fixPage(page), Cnd.where("parentId", "=", 0)));
	}

	@At
	@GET
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_LIST)
	public Result search(@Param("key") String key, @Param(value = "group", df = "0") int groupId, @Param(value = "page", df = "1") int page) {
		Cnd cnd = groupId == 0 ? Cnd.where("parentId", "=", 0) : Cnd.where("parentId", "=", 0).and("groupId", "=", groupId);
		Pager<CodeBook> pager = codebookService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), cnd, "name", "value");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_ADD)
	public Result save(CodeBook codebook) {
		return codebookService.save(codebook) == null ? Result.fail("保存数据失败!") : Result.success().addData("codebook", codebook);
	}

	@At("/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_EDIT)
	public Result detail(long id) {
		return Result.success().addData("codebook", codebookService.fetch(id));
	}

	@At("/delete/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_DELETE)
	public Result delete(long id) {
		return codebookService.delete(id) == 1 ? Result.success() : Result.fail("删除数据失败!");
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_EDIT)
	public Result update(CodeBook codebook) {
		return codebookService.updateIgnoreNull(codebook) != 1 ? Result.fail("更新数据失败!") : Result.success().addData("codebook", codebook);
	}

	@At("/top/?")
	@GET
	@ThunderRequiresPermissions(InstallPermission.CODEBOOK_EDIT)
	public Result top(long id) {
		return Result.success().addData("codes", codebookService.query(Cnd.where("groupId", "=", id).and("parentId", "=", 0)));
	}

	@At("/sub/?")
	@GET
	@ThunderRequiresPermissions(value = { InstallPermission.CODEBOOK_EDIT, InstallPermission.CODEBOOK_LIST }, logical = Logical.OR)
	public Result sub(long id) {
		return Result.success().addData("codes", codebookService.query(Cnd.where("parentId", "=", id)));
	}

}

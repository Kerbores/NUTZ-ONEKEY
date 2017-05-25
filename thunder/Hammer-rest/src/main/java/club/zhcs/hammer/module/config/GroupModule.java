package club.zhcs.hammer.module.config;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.bean.config.Group;
import club.zhcs.hammer.biz.config.GroupService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Pager;
import club.zhcs.titans.utils.db.Result;

/**
 * @author kerbores@gmail.com
 *
 */
@At("/group")
@Api(name = "Group", description = "码本分组相关接口")
public class GroupModule extends AbstractBaseModule {

	@Inject
	GroupService groupService;

	@At
	@GET
	public Result list(@Param(value = "page", df = "1") int page) {
		return Result.success().addData("pager", groupService.searchByPage(_fixPage(page)));
	}

	@At
	@GET
	public Result search(@Param("key") String key, @Param(value = "page", df = "1") int page) {
		Pager<Group> pager = groupService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name", "description");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public Result save(Group group) {
		return groupService.save(group) == null ? Result.fail("保存分组失败!") : Result.success().addData("group", group);
	}

	@At("/?")
	@GET
	public Result detail(long id) {
		return Result.success().addData("group", groupService.fetch(id));
	}

	@At("/delete/?")
	@GET
	public Result delete(long id) {
		return groupService.delete(id) == 1 ? Result.success() : Result.fail("删除分组失败!");
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public Result update(Group group) {
		return groupService.updateIgnoreNull(group) != 1 ? Result.fail("更新分组失败!") : Result.success().addData("group", group);
	}

	@At
	@GET
	public Result all() {
		return Result.success().addData("groups", groupService.queryAll());
	}

}

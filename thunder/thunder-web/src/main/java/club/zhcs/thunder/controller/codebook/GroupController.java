package club.zhcs.thunder.controller.codebook;

import org.apache.shiro.authz.annotation.Logical;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.config.Group;
import club.zhcs.thunder.biz.codebook.GroupService;
import club.zhcs.thunder.controller.base.BaseController;
import club.zhcs.thunder.ext.shiro.anno.SINORequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author kerbores
 *
 */
@RestController
@RequestMapping("group")
@Api(value = "Group", tags = { "码本分组" })
public class GroupController extends BaseController {

	@Autowired
	GroupService groupService;

	/**
	 * 列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.GROUP_LIST)
	@ApiOperation("码本分组列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", groupService.searchByPage(_fixPage(page), Cnd.NEW().desc("id")));
	}

	/**
	 * 搜索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.GROUP_LIST)
	@ApiOperation("码本分组搜索")
	public Result search(@RequestParam("key") @ApiParam("关键词") String key, @RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", groupService.searchByKeyAndPage(_fixSearchKey(key), _fixPage(page), "name", "description").addParam("key", key));
	}

	/**
	 * 添加
	 * 
	 * @param group
	 *            待添加数据
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.GROUP_ADD)
	@ApiOperation("添加码本分组")
	public Result save(@RequestBody Group group) {
		return groupService.save(group) == null ? Result.fail("保存分组失败!") : Result.success().addData("group", group);
	}

	/**
	 * 查看分组详情
	 * 
	 * @param id
	 *            分组id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(value = { InstallPermission.GROUP_EDIT, InstallPermission.GROUP_DETAIL }, logical = Logical.OR)
	@ApiOperation("码本分组详情")
	public Result detail(@PathVariable("id") @ApiParam("分组id") long id) {
		return Result.success().addData("group", groupService.fetch(id));
	}

	/**
	 * 切换分组状态
	 * 
	 * @param id
	 *            分组id
	 * @param status
	 *            状态
	 * @return
	 */
	@GetMapping("toggle/{id}/{status}")
	@SINORequiresPermissions(InstallPermission.GROUP_DELETE)
	@ApiOperation("设置码本分组状态")
	public Result delete(@PathVariable("id") @ApiParam("分组id") long id, @PathVariable("status") @ApiParam("状态") boolean status) {
		return groupService.update(Chain.make("delete", status), Cnd.where("id", "=", id)) == 1 ? Result.success() : Result.fail("删除分组失败!");
	}

	/**
	 * 编辑码本分组
	 * 
	 * @param group
	 *            待编辑分组
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.GROUP_EDIT)
	@ApiOperation("编辑码本分组")
	public Result update(@RequestBody Group group) {
		return groupService.updateIgnoreNull(group) != 1 ? Result.fail("更新分组失败!") : Result.success().addData("group", group);
	}

	/**
	 * 分组列表
	 * 
	 * @return
	 */
	@GetMapping("all")
	@ApiOperation("全部分组数据")
	public Result all() {
		return Result.success().addData("groups", groupService.queryAll());
	}
}

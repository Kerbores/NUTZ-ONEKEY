package club.zhcs.thunder.controller.struts;

import org.apache.shiro.authz.annotation.Logical;
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
import club.zhcs.thunder.bean.struts.Branch;
import club.zhcs.thunder.biz.struts.BranchService;
import club.zhcs.thunder.controller.base.BaseController;
import club.zhcs.thunder.ext.shiro.anno.SINORequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@RestController
@RequestMapping("branch")
@Api(value = "Branch", tags = { "分支机构模块" })
public class BranchController extends BaseController {

	@Autowired
	BranchService branchService;

	/**
	 * 机构列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.BRANCH_LIST)
	@ApiOperation("机构列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", branchService.searchByPage(fixPage(page), Cnd.where("parentId", "=", 0)));
	}

	/**
	 * 机构检索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.BRANCH_LIST)
	@ApiOperation("机构检索")
	public Result search(
			@RequestParam("key") @ApiParam("关键词") String key,
			@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager",
				branchService.searchByKeyAndPage(fixSearchKey(key), fixPage(page), Cnd.where("parentId", "=", 0), "name", "description", "address").addParam("key", key));
	}

	/**
	 * 添加机构
	 * 
	 * @param branch
	 *            待添加机构
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.BRANCH_ADD)
	@ApiOperation("添加机构")
	public Result save(@RequestBody Branch branch) {
		return branchService.save(branch) == null ? Result.fail("保存数据失败!") : Result.success().addData("branch", branch);
	}

	@GetMapping("all")
	@ApiOperation("机构详情")
	public Result all() {
		return Result.success().addData("branchs", branchService.query());
	}

	/**
	 * 机构数据详情
	 * 
	 * @param id
	 *            机构id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(InstallPermission.BRANCH_EDIT)
	@ApiOperation("机构详情")
	public Result detail(@PathVariable("id") @ApiParam("机构id") long id) {
		return Result.success().addData("branch", branchService.fetch(id));
	}

	/**
	 * 删除机构
	 * 
	 * @param id
	 *            机构id
	 * @return
	 */
	@GetMapping("delete/{id}")
	@SINORequiresPermissions(InstallPermission.BRANCH_DELETE)
	@ApiOperation("删除机构")
	public Result delete(@PathVariable("id") @ApiParam("机构id") long id) {
		return branchService.delete(id) == 1 ? Result.success() : Result.fail("删除数据失败!");
	}

	/**
	 * 编辑机构
	 * 
	 * @param branch
	 *            待编辑机构
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.BRANCH_EDIT)
	@ApiOperation("编辑机构")
	public Result update(@RequestBody Branch branch) {
		return branchService.updateIgnoreNull(branch) != 1 ? Result.fail("更新数据失败!") : Result.success().addData("branch", branch);
	}

	/**
	 * 一级机构列表
	 * 
	 * @return
	 */
	@GetMapping("top")
	@SINORequiresPermissions(InstallPermission.BRANCH_EDIT)
	@ApiOperation("一级机构列表")
	public Result top() {
		return Result.success().addData("tops", branchService.query(Cnd.where("parentId", "=", 0)));
	}

	/**
	 * 获取指定机构的下级机构
	 * 
	 * @param id
	 *            机构id
	 * @return
	 */
	@GetMapping("sub/{id}")
	@SINORequiresPermissions(value = { InstallPermission.BRANCH_EDIT, InstallPermission.BRANCH_LIST }, logical = Logical.OR)
	@ApiOperation("获取指定机构的下级机构")
	public Result sub(@PathVariable("id") @ApiParam("机构id") long id) {
		return Result.success().addData("branchs", branchService.query(Cnd.where("parentId", "=", id)));
	}
}

package club.zhcs.thunder.controller.struts;

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
import club.zhcs.thunder.bean.struts.Department;
import club.zhcs.thunder.biz.struts.DepartmentService;
import club.zhcs.thunder.controller.base.BaseController;
import club.zhcs.thunder.ext.shiro.anno.SINORequiresPermissions;
import club.zhcs.thunder.vo.InstallPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author kerbores@gmail.com
 *
 */
@RestController
@RequestMapping("dept")
@Api(value = "Department", tags = { "部门管理模块" })
public class DepartmentController extends BaseController {

	@Autowired
	DepartmentService departmentService;

	/**
	 * 部门列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("list")
	@SINORequiresPermissions(InstallPermission.DEPT_LIST)
	@ApiOperation("部门列表")
	public Result list(@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager", departmentService.searchByPage(fixPage(page)));
	}

	/**
	 * 部门检索
	 * 
	 * @param key
	 *            关键词
	 * @param page
	 *            页码
	 * @return
	 */
	@GetMapping("search")
	@SINORequiresPermissions(InstallPermission.DEPT_LIST)
	@ApiOperation("部门检索")
	public Result search(
			@RequestParam("key") @ApiParam("关键词") String key,
			@RequestParam(value = "branchId", defaultValue = "0") @ApiParam("机构id") long branchId,
			@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page) {
		return Result.success().addData("pager",
				departmentService.searchByKeyAndPage(
						fixSearchKey(key),
						fixPage(page),
						branchId == 0 ? null : Cnd.where("branchId", "=", branchId),
						"name",
						"description")
						.addParam("key", key));
	}

	/**
	 * 添加部门
	 * 
	 * @param department
	 *            待添加部门
	 * @return
	 */
	@PostMapping("add")
	@SINORequiresPermissions(InstallPermission.DEPT_ADD)
	@ApiOperation("添加部门")
	public Result save(@RequestBody Department department) {
		return departmentService.save(department) == null ? Result.fail("保存数据失败!") : Result.success().addData("department", department);
	}

	/**
	 * 部门数据详情
	 * 
	 * @param id
	 *            部门id
	 * @return
	 */
	@GetMapping("{id}")
	@SINORequiresPermissions(InstallPermission.DEPT_EDIT)
	@ApiOperation("部门详情")
	public Result detail(@PathVariable("id") @ApiParam("部门id") long id) {
		return Result.success().addData("department", departmentService.fetch(id));
	}

	/**
	 * 删除部门
	 * 
	 * @param id
	 *            部门id
	 * @return
	 */
	@GetMapping("delete/{id}")
	@SINORequiresPermissions(InstallPermission.DEPT_DELETE)
	@ApiOperation("删除部门")
	public Result delete(@PathVariable("id") @ApiParam("部门id") long id) {
		return departmentService.delete(id) == 1 ? Result.success() : Result.fail("删除数据失败!");
	}

	/**
	 * 编辑部门
	 * 
	 * @param department
	 *            待编辑部门
	 * @return
	 */
	@PostMapping("edit")
	@SINORequiresPermissions(InstallPermission.DEPT_EDIT)
	@ApiOperation("编辑部门")
	public Result update(@RequestBody Department department) {
		return departmentService.updateIgnoreNull(department) != 1 ? Result.fail("更新数据失败!") : Result.success().addData("department", department);
	}
}

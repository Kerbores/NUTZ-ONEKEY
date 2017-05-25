package club.zhcs.hammer.module.struts;

import org.nutz.mvc.annotation.At;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.titans.nutz.module.base.AbstractBaseModule;

@At("department")
@Api(name = "Department", description = "部门接口")
public class DepartmentModule extends AbstractBaseModule {

}

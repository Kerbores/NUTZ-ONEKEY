package club.zhcs.hammer.ext.shiro.aop;

import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description //TODO description
 * 
 * @copyright copyright©2016 zhcs.club
 *
 * @createTime 2016年8月23日 下午1:13:11
 */
public class ThunderRoleAnnotationMethodInterceptor extends RoleAnnotationMethodInterceptor {

	public ThunderRoleAnnotationMethodInterceptor() {
		setHandler(new ThunderRoleAnnotationHandler());
	}

	public ThunderRoleAnnotationMethodInterceptor(AnnotationResolver resolver) {
		setHandler(new ThunderRoleAnnotationHandler());
		setResolver(resolver);
	}
}

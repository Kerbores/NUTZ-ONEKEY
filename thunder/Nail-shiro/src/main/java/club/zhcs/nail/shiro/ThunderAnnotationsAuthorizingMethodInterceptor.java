package club.zhcs.nail.shiro;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.shiro.aop.AnnotationResolver;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;
import org.apache.shiro.authz.aop.AuthenticatedAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.GuestAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.PermissionAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.RoleAnnotationMethodInterceptor;
import org.apache.shiro.authz.aop.UserAnnotationMethodInterceptor;
import org.apache.shiro.spring.aop.SpringAnnotationResolver;

import club.zhcs.nail.shiro.aop.ThunderPermissionAnnotationMethodInterceptor;
import club.zhcs.nail.shiro.aop.ThunderRoleAnnotationMethodInterceptor;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
public class ThunderAnnotationsAuthorizingMethodInterceptor extends AnnotationsAuthorizingMethodInterceptor implements MethodInterceptor {

	public ThunderAnnotationsAuthorizingMethodInterceptor() {
		List interceptors = new ArrayList(7);

		AnnotationResolver resolver = new SpringAnnotationResolver();

		interceptors.add(new RoleAnnotationMethodInterceptor(resolver));
		interceptors.add(new PermissionAnnotationMethodInterceptor(resolver));
		interceptors.add(new AuthenticatedAnnotationMethodInterceptor(resolver));
		interceptors.add(new UserAnnotationMethodInterceptor(resolver));
		interceptors.add(new GuestAnnotationMethodInterceptor(resolver));
		interceptors.add(new ThunderRoleAnnotationMethodInterceptor(resolver));
		interceptors.add(new ThunderPermissionAnnotationMethodInterceptor(resolver));

		setMethodInterceptors(interceptors);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.aopalliance.intercept.MethodInterceptor#invoke(org.aopalliance.
	 * intercept.MethodInvocation)
	 */
	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		org.apache.shiro.aop.MethodInvocation mi = createMethodInvocation(methodInvocation);
		assertAuthorized(mi);
		return methodInvocation.proceed();
	}

	@Override
	protected void assertAuthorized(org.apache.shiro.aop.MethodInvocation methodInvocation) throws AuthorizationException {
		Collection<AuthorizingAnnotationMethodInterceptor> aamis = getMethodInterceptors();
		if ((aamis != null) && (!(aamis.isEmpty())))
			for (AuthorizingAnnotationMethodInterceptor aami : aamis)
				if (aami.supports(methodInvocation))
					aami.assertAuthorized(methodInvocation);
	}

	/**
	 * @param methodInvocation
	 * @return
	 */
	private org.apache.shiro.aop.MethodInvocation createMethodInvocation(final MethodInvocation methodInvocation) {
		org.apache.shiro.aop.MethodInvocation mi = new org.apache.shiro.aop.MethodInvocation() {

			@Override
			public Object proceed() throws Throwable {
				return methodInvocation.proceed();
			}

			@Override
			public Object getThis() {
				return methodInvocation.getThis();
			}

			@Override
			public Method getMethod() {
				return methodInvocation.getMethod();
			}

			@Override
			public Object[] getArguments() {
				return methodInvocation.getArguments();
			}
		};
		return mi;
	}
}

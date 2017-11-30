package club.zhcs.thunder.ext.shiro.aop;

import java.lang.annotation.Annotation;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.aop.RoleAnnotationHandler;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

import club.zhcs.thunder.ext.shiro.anno.SINORequiresRoles;
import club.zhcs.thunder.vo.InstalledRole;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public class SINORoleAnnotationHandler extends RoleAnnotationHandler {

	public SINORoleAnnotationHandler() {
		setAnnotationClass(SINORequiresRoles.class);
	}

	@Override
	public void assertAuthorized(Annotation a) throws AuthorizationException {
		if (!(a instanceof SINORequiresRoles))
			return;

		SINORequiresRoles rpAnnotation = (SINORequiresRoles) a;
		InstalledRole[] roles_ = rpAnnotation.value();
		Subject subject = getSubject();

		final String[] roles = new String[roles_.length];

		Lang.each(roles_, new Each<InstalledRole>() {

			@Override
			public void invoke(int index, InstalledRole ele, int length) throws ExitLoop, ContinueLoop, LoopException {
				roles[index] = ele.getName();
			}
		});

		if (roles.length == 1) {
			subject.checkRole(roles[0]);
			return;
		}
		if (Logical.AND.equals(rpAnnotation.logical())) {
			getSubject().checkRoles(roles);
			return;
		}
		if (Logical.OR.equals(rpAnnotation.logical())) {
			boolean hasAtLeastOneRoles = false;
			for (String role : roles)
				if (getSubject().hasRole(role))
					hasAtLeastOneRoles = true;
			if (!hasAtLeastOneRoles)
				getSubject().checkRole(roles[0]);
		}
	}
}

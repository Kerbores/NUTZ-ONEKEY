package club.zhcs.hammer.config;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;

import org.nutz.integration.shiro.ShiroFilter2;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@WebFilter(filterName = "thunder-shiro", description = "thunder-shiro", urlPatterns = "/*", dispatcherTypes = {
		DispatcherType.REQUEST,
		DispatcherType.FORWARD,
		DispatcherType.ERROR,
		DispatcherType.INCLUDE })
public class A_ShiroFilter extends ShiroFilter2 {

}

package club.zhcs.hammer.config;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.nutz.mvc.NutFilter;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@WebFilter(description = "Nutz", urlPatterns = "/*", dispatcherTypes = {
		DispatcherType.REQUEST,
		DispatcherType.FORWARD,
		DispatcherType.INCLUDE }, initParams = {
				@WebInitParam(name = "modules", value = "club.zhcs.hammer.module.MainModule"),
				@WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/sigar/*")
		})
public class C_HammerFilter extends NutFilter {

}

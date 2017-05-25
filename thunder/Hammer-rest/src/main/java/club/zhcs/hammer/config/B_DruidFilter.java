package club.zhcs.hammer.config;

import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import com.alibaba.druid.support.http.WebStatFilter;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@WebFilter(filterName = "druid", description = "druid", urlPatterns = "/*", dispatcherTypes = {
		DispatcherType.REQUEST,
		DispatcherType.FORWARD,
		DispatcherType.INCLUDE,
		DispatcherType.ERROR
}, initParams = {
		@WebInitParam(name = "exclusions", value = "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*,/sigar")
})
public class B_DruidFilter extends WebStatFilter {

}

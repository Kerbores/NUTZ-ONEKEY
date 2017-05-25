package club.zhcs.hammer.config;

import javax.servlet.annotation.WebServlet;

import com.alibaba.druid.support.http.StatViewServlet;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
@WebServlet(name = "Druid", urlPatterns = "/druid/*")
public class HammerDruidServlet extends StatViewServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8739395139842244315L;

}

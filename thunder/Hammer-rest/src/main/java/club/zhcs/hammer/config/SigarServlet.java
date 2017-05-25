package club.zhcs.hammer.config;

import javax.servlet.annotation.WebServlet;

/**
 * @author kerbores@gmail.com
 *
 */
@WebServlet(name = "Sigar", urlPatterns = "/sigar/api")
public class SigarServlet  extends club.zhcs.titans.gather.servlet.SigarServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}

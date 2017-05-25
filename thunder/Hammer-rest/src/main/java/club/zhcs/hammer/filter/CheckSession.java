package club.zhcs.hammer.filter;

import javax.servlet.http.HttpSession;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.view.HttpStatusView;

/**
 * @author kerbores@gmail.com
 *
 */
public class CheckSession implements ActionFilter {

	private String name;
	private int code;

	public CheckSession(String name, int code) {
		this.name = name;
		this.code = code;
	}

	@Override
	public View match(ActionContext context) {
		HttpSession session = Mvcs.getHttpSession(false);
		if (session == null || null == session.getAttribute(name))
			return new HttpStatusView(code);
		return null;
	}

}

package club.zhcs.thunder.config.wechat;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.web.servlet.View;

/**
 * @author kerbores
 *
 */
public class NutzViewWrapper implements View {

	org.nutz.mvc.View view;

	Log log = Logs.get();

	/**
	 * @param view
	 */
	public NutzViewWrapper(org.nutz.mvc.View view) {
		super();
		this.view = view;
	}

	/**
	 * @return the view
	 */
	public org.nutz.mvc.View getView() {
		return view;
	}

	/**
	 * @param view
	 *            the view to set
	 */
	public void setView(org.nutz.mvc.View view) {
		this.view = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.View#getContentType()
	 */
	@Override
	public String getContentType() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.servlet.View#render(java.util.Map,
	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF8");
		try {
			view.render(request, response, model);
		} catch (Throwable e) {
			log.debug(e);
			e.printStackTrace();
		}
	}

}

package club.zhcs.thunder.module;

import javax.servlet.http.HttpServletRequest;

import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import club.zhcs.titans.nutz.module.base.AbstractBaseModule;

/**
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@At("/")
public class RobotModule extends AbstractBaseModule {

	@At("robot/*")
	@Ok("raw")
	@Fail("void")
	@Filters
	public String msg(@Param("..") NutMap data, HttpServletRequest req) {

		log.debug(data);

		if (!data.getString("Message").startsWith("#")) {
			return "";
		}
		String key = data.getString("Message").substring(1);
		Response resp = Http.get("http://nutz.cn/yvr/search?q=" + key + "&format=json");
		final StringBuilder msgbBuilder = new StringBuilder(String.format("@%s(%s)请关注以下帖子内容:\r\n", data.getString("SenderName"), data.getString("Sender")));
		if (resp.isOK()) {
			NutMap suggestions = Lang.map(resp.getContent());
			Lang.each(suggestions.getList("suggestions", NutMap.class), new Each<NutMap>() {

				@Override
				public void invoke(int arg0, NutMap suggestion, int arg2) throws ExitLoop, ContinueLoop, LoopException {
					if (Strings.equals(suggestion.getString("type"), "ask")) {
						msgbBuilder.append(suggestion.getString("title") + " : https://nutz.cn/yvr/t/" + suggestion.getString("contentId") + "\r\n");
					}
				}
			});
			msgbBuilder.append("论坛完整检索结果: https://nutz.cn/yvr/search?q=" + key);
			return msgbBuilder.toString();
		}
		return "nutz.cn 正在打盹,请稍后再试!";
	}
}

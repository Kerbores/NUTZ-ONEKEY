package club.zhcs.thunder.controller.wechat;

import java.io.IOException;

import org.nutz.dao.Cnd;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import club.zhcs.common.Result;
import club.zhcs.thunder.bean.qa.Nutzer;
import club.zhcs.thunder.biz.qa.NutzerService;
import club.zhcs.thunder.controller.base.BaseController;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@RestController
@RequestMapping("qa")
@Api(value = "QA", tags = { "Nutz.cn接口" })
public class QAController extends BaseController {

	@Autowired
	NutzerService nutzerService;

	/**
	 * 帖子列表
	 * 
	 * @param page
	 *            页码
	 * @param tab
	 *            分类
	 * @param tag
	 *            标签
	 * @param search
	 *            关键词
	 * @param limit
	 *            页面大小
	 * @return
	 */
	@GetMapping("topic")
	@ApiOperation("帖子列表")
	public Result topic(
			@RequestParam(value = "page", defaultValue = "1") @ApiParam("页码") int page,
			@RequestParam(value = "tab", required = false) @ApiParam("分类") String tab,
			@RequestParam(value = "tag", required = false) @ApiParam("标签") String tag,
			@RequestParam(value = "search", required = false) @ApiParam("关键词") String search,
			@RequestParam(value = "limit", defaultValue = "5") @ApiParam("页面大小") int limit) {
		UserAgent ua = ua();
		Logs.get().debugf("%s,%s,%s", ua.getBrowser(), ua.getBrowserVersion(), ua.getOperatingSystem());
		String topicApi = "https://nutz.cn/yvr/api/v1/topics?page=" + page + "&limit=" + limit;
		if (Strings.isNotBlank(tab)) {
			topicApi += "&tab=" + tab;
		}
		if (Strings.isNotBlank(tag)) {
			topicApi += "&tag=" + tag;
		}
		if (Strings.isNotBlank(search)) {
			topicApi += "&search=" + search;
		}
		return Result.success()
				.addData("topics", Json.fromJson(Http.get(topicApi).getContent()))
				.addData("page", page)
				.addData("tab", tab)
				.addData("tag", tag)
				.addData("search", search)
				.addData("limit", limit);
	}

	/**
	 * 帖子详情
	 * 
	 * @param id
	 *            帖子id
	 * @param nutzer
	 * @return
	 */
	@GetMapping("detail/{id}")
	@ApiOperation("帖子详情")
	public Result detail(@PathVariable("id") @ApiParam("帖子id") String id) {
		return Result.success()
				.addData("topic", Json.fromJson(Http.get("https://nutz.cn/yvr/api/v1/topic/" + id).getContent()));
	}

	/**
	 * 发帖
	 * 
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param nutzer
	 * @return
	 */
	@PostMapping("add")
	@ApiOperation("发帖")
	public Result topic(
			@RequestParam("title") @ApiParam("标题") String title,
			@RequestParam("tab") @ApiParam("分类") String tab,
			@RequestParam("content") @ApiParam("内容") String content,
			@RequestParam("token") @ApiParam("token") String token) {
		if (Strings.isBlank(token))
		{
			return Result.fail("非法用户");
		}
		Response response = Http.post2("https://nutz.cn/yvr/api/v1/topics",
				NutMap.NEW().addv("title", title).addv("content", content).addv("type", tab).addv("accesstoken", token),
				5000);
		if (response.isOK()) {
			return Result.success();
		}
		return Result.fail("发帖失败!");
	}

	/**
	 * 发表回帖
	 * 
	 * @param id
	 *            帖子id
	 * @param content
	 *            回帖内容
	 * @param nutzer
	 * @return
	 */
	@GetMapping("reply")
	@ApiOperation("发表回帖")
	public Result reply(@RequestParam("id") @ApiParam("帖子id") String id,
			@RequestParam("content") @ApiParam("回帖内容") String content,
			@RequestParam("token") @ApiParam("token") String token) {
		if (Strings.isBlank(token)) {
			return Result.fail("非法用户");
		}
		Response response = Http.post2("https://nutz.cn/yvr/api/v1/topic/" + id + "/replies",
				NutMap.NEW().addv("id", id).addv("content", content + "<br>来自 NB 哄哄的 nutz-onekey 微信客户端!").addv("accesstoken", token),
				5000);
		if (response.isOK()) {
			return Result.success();
		}
		return Result.fail("回复失败!");
	}

	@PostMapping("upload")
	@ApiOperation("上传图片")
	public NutMap upload(MultipartFile img, @RequestParam("token") @ApiParam("token") String token) throws IOException {
		if (Strings.isBlank(token)) {
			return NutMap.NEW().addv("success", 0).addv("message", "用户不存在!");
		}
		NutMap paras = NutMap.NEW();
		// TODO 文件处理
		paras.addv("file", null);
		Response response = Http.upload("https://nutz.cn/yvr/api/v1/images?accesstoken=" + token, paras, Header.create(), 100000);
		if (response.isOK()) {
			return NutMap.NEW().addv("success", 1).addv("message", "上传成功!").addv("url", "https://nutz.cn" + Lang.map(response.getContent()).getString("url"));
		}
		return NutMap.NEW().addv("success", 0).addv("message", "上传失败!<br>code:" + response.getStatus());
	}

	/**
	 * 获取个人信息
	 * 
	 * @param nutzer
	 * @return
	 */
	@GetMapping("nutzer/{openid}")
	@ApiOperation("获取个人信息")
	public Result me(@PathVariable("openid") String openid) {
		Nutzer nutzer = nutzerService.fetch(Cnd.where("openid", "=", openid));
		if (nutzer == null) {
			return Result.success();
		}
		Response response = Http.get("https://nutz.cn/yvr/api/v1/accesstoken?accesstoken=" + nutzer.getAccessToken());
		if (response.isOK()) {
			NutMap data = Lang.map(response.getContent());
			String loginname = data.getString("loginname");
			response = Http.get("https://nutz.cn/yvr/api/v1/user/" + loginname);
			if (response.isOK()) {
				return Result.success().addData("nutzer", Lang.map(response.getContent()).setv("wechatUser", nutzer));
			}
		}
		return Result.success().addData("nutzer", NutMap.NEW().setv("wechatUser", nutzer));
	}

	/**
	 * 用户绑定
	 * 
	 * @param token
	 * @param nutzer
	 * @return
	 */
	@GetMapping("bind")
	@ApiOperation("绑定用户")
	public @ResponseBody Result bind(@Param("token") String token, @Param("openid") String openid) {
		Nutzer nutzer = nutzerService.fetch(Cnd.where("openid", "=", openid));
		if (nutzer == null) {
			return Result.fail("不存在的用户,请重新关注公众号!");
		}
		Response response = Http.post2("https://nutz.cn/yvr/api/v1/accesstoken", NutMap.NEW().addv("accesstoken", token), 5000);
		if (response.isOK()) {
			NutMap data = Lang.map(response.getContent());
			if (data.getBoolean("success")) {
				// 更新信息到NUTZER
				Response r1 = Http.get("https://nutz.cn/yvr/api/v1/user/" + data.getString("loginname"));
				NutMap userInfo = Lang.map(r1.getContent());
				nutzer.setAccessToken(token);
				nutzer.setLoginName(userInfo.getAs("data", NutMap.class).getString("loginname"));
				nutzer.setAvatarUrl(userInfo.getAs("data", NutMap.class).getString("avatar_url"));
				nutzerService.update(nutzer);
			}
			return Result.success().addData(data);
		}
		return Result.fail("token不正确!");
	}
}

package club.zhcs.thunder.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.dao.Cnd;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.img.Images;
import org.nutz.lang.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import club.zhcs.captcha.DefaultCaptchaGener;
import club.zhcs.common.Result;
import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.QiniuUploader;
import club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.R;
import club.zhcs.thunder.config.qiniu.QiuniuConfigProperties;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Controller
@RequestMapping("image")
public class ImageController {
	@Autowired
	QiniuUploader qiniuUploader;

	@Autowired
	UserService userService;
	@Autowired
	QiuniuConfigProperties qiuniuConfigProperties;

	@PostMapping("upload")
	public @ResponseBody Result test(MultipartFile file) throws IOException {
		R r = qiniuUploader.upload(file.getInputStream());
		return r == null ? Result.fail("上传失败!") : Result.success().addData("url", r.getUrl());
	}

	@PostMapping("avatar")
	@RequiresUser
	public @ResponseBody Result setAvatar(MultipartFile file) throws IOException {
		R r = qiniuUploader.upload(file.getInputStream());
		User user = userService.fetch(Cnd.where("name", "=", SecurityUtils.getSubject().getPrincipal()));
		user.setHeadKey(r.getKey());
		userService.update(user);
		return Result.success().addData("r", r);
	}

	@GetMapping("/{uuid}")
	public void show(@PathVariable("uuid") String uuid, HttpServletResponse httpServletResponse) throws IOException {
		Response response = Http.get(String.format("%s%s", qiuniuConfigProperties.getDomain(), uuid));
		if (response.isOK()) {
			Streams.writeAndClose(httpServletResponse.getOutputStream(), response.getStream());
		} else {
			Images.write(Images.createAvatar(new DefaultCaptchaGener("NUTZONEKEY").gen(3)), "png", httpServletResponse.getOutputStream());
		}
	}

	@GetMapping("avatar")
	@RequiresUser
	public void avatar(@RequestParam(value = "id", required = false, defaultValue = "0") long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = null;
		if (id == 0) {
			user = userService.fetch(Cnd.where("name", "=", SecurityUtils.getSubject().getPrincipal()));
		} else {
			user = userService.fetch(id);
		}
		Images.write(Images.createAvatar(user == null ? new DefaultCaptchaGener("NUTZONEKEY").gen(3) : user.getNickName()), "png", response.getOutputStream());
	}
}

package club.zhcs.nail.controller;

import java.io.IOException;

import org.nutz.mvc.annotation.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import club.zhcs.nail.ThunderApplication.SessionKeys;
import club.zhcs.nail.bean.acl.User;
import club.zhcs.nail.biz.QiniuUploader;
import club.zhcs.nail.biz.QiniuUploader.R;
import club.zhcs.nail.biz.acl.UserService;
import club.zhcs.titans.utils.db.Result;

@RequestMapping("file")
public class FileController {

	@Autowired
	QiniuUploader qiniuUploader;

	@Autowired
	UserService userService;

	@PostMapping("upload")
	public Result upload(@Param("file") MultipartFile f, @SessionAttribute(SessionKeys.USER_KEY) User user) throws IOException {
		R r = qiniuUploader.upload(f.getInputStream());
		if (r == null) {
			return Result.fail("上传失败!");
		}
		user.setHeadKey(r.getKey());
		return userService.update(user, "headKey") != 1 ? Result.fail("更新失败!") : Result.success().addData("url", r.getUrl());
	}

}

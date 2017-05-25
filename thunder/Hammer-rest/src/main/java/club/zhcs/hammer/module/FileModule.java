package club.zhcs.hammer.module;

import java.io.IOException;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.plugins.apidoc.annotation.Api;

import club.zhcs.hammer.ThunderApplication.SessionKeys;
import club.zhcs.hammer.bean.acl.User;
import club.zhcs.hammer.biz.QiniuUploader;
import club.zhcs.hammer.biz.QiniuUploader.R;
import club.zhcs.hammer.biz.acl.UserService;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.utils.db.Result;

@At("file")
@Api(name = "File", description = "文件相关接口")
public class FileModule extends AbstractBaseModule {

	@Inject
	QiniuUploader qiniuUploader;

	@Inject
	UserService userService;

	@At
	@POST
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
	public Result upload(@Param("file") TempFile f, @Attr(SessionKeys.USER_KEY) User user) throws IOException {
		R r = qiniuUploader.upload(f.getInputStream());
		if (r == null) {
			return Result.fail("上传失败!");
		}
		user.setHeadKey(r.getKey());
		return userService.update(user, "headKey") != 1 ? Result.fail("更新失败!") : Result.success().addData("url", r.getUrl());
	}

}

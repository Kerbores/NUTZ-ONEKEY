package club.zhcs.thunder.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import club.zhcs.common.Result;

/**
 * @author kerbores
 *
 */
@RestController
@RequestMapping("file")
public class UploadController {
	@Autowired
	club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.QiniuUploader qiniuUploader;

	@PostMapping("upload")
	public Result test(MultipartFile file) throws IOException {
		club.zhcs.thunder.config.qiniu.QiniuAutoConfiguration.R r = qiniuUploader.upload(file.getInputStream());
		return r == null ? Result.fail("上传失败!") : Result.success().addData("url", r.getUrl());
	}
}

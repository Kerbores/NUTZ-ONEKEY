package club.zhcs.thunder.module;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.http.sender.PostSender;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.repo.Base64;

import com.google.common.collect.Lists;

import club.zhcs.thunder.module.FileModule.BaiduOcr.IDCard.Sex;
import club.zhcs.titans.nutz.module.base.AbstractBaseModule;
import club.zhcs.titans.qiniu.QiNiuUploader;
import club.zhcs.titans.utils.db.Result;
import club.zhcs.titans.utils.file.Images;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project app
 *
 * @file FileUploadModule.java
 *
 * @description 文件上传下载
 *
 * @time 2016年3月24日 下午1:46:46
 *
 */
@Filters
public class FileModule extends AbstractBaseModule {

	public static class BaiduOcr {

		public static class IDCard {

			public static enum Sex {

				MALE("男"), FEMALE("女");
				private String name;

				public String getName() {
					return name;
				}

				public void setName(String name) {
					this.name = name;
				}

				private Sex(String name) {
					this.name = name;
				}

			}

			private String name;
			private Sex sex;
			private String volk;
			private Date birthDay;
			private String address;
			private String id;

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public Sex getSex() {
				return sex;
			}

			public void setSex(Sex sex) {
				this.sex = sex;
			}

			public String getVolk() {
				return volk;
			}

			public void setVolk(String volk) {
				this.volk = volk;
			}

			public Date getBirthDay() {
				return birthDay;
			}

			public void setBirthDay(Date birthDay) {
				this.birthDay = birthDay;
			}

			public String getAddress() {
				return address;
			}

			public void setAddress(String address) {
				this.address = address;
			}

			public String getId() {
				return id;
			}

			public void setId(String id) {
				this.id = id;
			}

			@Override
			public String toString() {
				return Json.toJson(this);
			}

		}

		@Deprecated
		public static IDCard query(String id) {
			Map<String, String> header = new HashMap<String, String>();
			header.put("apikey", "4a6cfff3a7840913094a7cbb67d79537");// appkey
			Request request = Request.create("http://apis.baidu.com/apistore/idservice/id?id=" + id, METHOD.GET,
					NutMap.NEW(), Header.create(header));
			Sender sender = PostSender.create(request);
			Response response = sender.send();
			String info = response.getContent();
			NutMap data = Lang.map(info);
			System.err.println(Json.toJson(data));
			return null;
		}

		public static void main(String[] args) {
			System.err.println(ocr("D://u=573599941,1522284854&fm=23&gp=0.jpg"));
		}

		/**
		 * ocr识别
		 * 
		 * @param path
		 *            图片文件路径
		 * @return 识别结果
		 */
		public static NutMap ocr(String path) {
			return ocrBase64(Images.GetImageStr(new File(path)));
		}

		/**
		 * 识别base64信息
		 * 
		 * @param imgStr
		 * @return
		 */
		public static NutMap ocrBase64(String imgStr) {
			NutMap params = NutMap.NEW();
			Map<String, String> header = new HashMap<String, String>();
			header.put("apikey", "5f3fb5d03229507d70d6d6b165a5ec21");// appkey
																		// 目前是每天5000次的调用
			params.put("fromdevice", "pc");
			params.put("clientip", "10.0.1.1");
			params.put("detecttype", "LocateRecognize");
			params.put("languagetype", "CHN_ENG");
			params.put("imagetype", "1");
			params.put("image", imgStr);

			Request request = Request.create("http://apis.baidu.com/apistore/idlocr/ocr", METHOD.POST, params,
					Header.create(header));
			Sender sender = PostSender.create(request);
			Response response = sender.send();
			String info = response.getContent();

			return Lang.map(info);
		}

		public static IDCard toCard(NutMap ocrResult) {
			final IDCard card = new IDCard();
			Lang.each(ocrResult.getList("retData", Map.class), new Each<Map>() {

				@Override
				public void invoke(int index, Map map, int length) throws ExitLoop, ContinueLoop, LoopException {
					NutMap data = NutMap.WRAP(map);
					String word = data.getString("word");
					if (word.indexOf("姓名") >= 0) {
						try {
							card.name = word.substring(word.indexOf("姓名") + 2);
						} catch (Exception e) {
							Logs.get().equals(e);
						}
					} else if (word.indexOf("性别") >= 0 || word.indexOf("民族") >= 0) {
						card.sex = word.indexOf("男") >= 0 ? Sex.MALE : Sex.FEMALE;
						if (word.indexOf("族") >= 0) {
							try {
								card.volk = word.substring(word.indexOf("族") + 1);
							} catch (Exception e) {
								Logs.get().equals(e);
							}
						}
					} else if (word.indexOf("出生") >= 0) {
						try {
							card.birthDay = Times.D(word.substring(word.indexOf("出生") + 2).replace("年", "-")
									.replace("月", "-").replace("日", "").replace("[\u4E00-\u9FA5]+", "").trim());
						} catch (Exception e) {
							Logs.get().equals(e);
						}
					} else if (word.indexOf("身份号码") >= 0) {
						try {
							card.id = word.substring(word.indexOf("身份号码") + 4);
						} catch (Exception e) {
							Logs.get().equals(e);
						}
					} else if (word.indexOf("住址") >= 0) {
						if (Strings.isBlank(card.address)) {
							try {
								card.address = word.substring(word.indexOf("住址") + 2);
							} catch (Exception e) {
								Logs.get().equals(e);
							}
						} else {
							try {
								card.address = card.address = word.substring(word.indexOf("住址") + 2) + card.address;
							} catch (Exception e) {
								Logs.get().equals(e);
							}
						}
					} else {
						if (Strings.isBlank(card.address)) {
							card.address = word;
						} else {
							card.address += word;
						}
					}
				}
			});
			return card;
		}

	}

	@At("/idcard")
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
	public NutMap idCard(@Param("img") TempFile imgFile) throws IOException {
		byte[] data = null;
		InputStream in = imgFile.getInputStream();
		data = new byte[in.available()];
		in.read(data);
		in.close();
		String base64 = Base64.encodeToString(data, false);
		return BaiduOcr.ocr(base64);
	}

	@Inject
	QiNiuUploader uploader;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kerbores.nutz.module.base.AbstractBaseModule#_getNameSpace()
	 */
	@Override
	public String _getNameSpace() {
		return null;
	}

	@At("/kind/upload")
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
	public NutMap upload(TempFile imgFile) throws IOException {
		String key = uploader.upload(Streams.readBytes(imgFile.getInputStream()));
		if (Strings.isBlank(key)) {
			return NutMap.NEW().addv("error", 1).addv("message", "上传失败");
		}
		return NutMap.NEW().addv("error", 0).addv("url", uploader.privateUrl(key));
	}

	/**
	 * ajax 文件上传
	 * 
	 * @param files
	 * @return
	 * @throws IOException
	 */
	@At
	@AdaptBy(type = UploadAdaptor.class, args = { "${app.root}/WEB-INF/tmp" })
	public Result upload(@Param("files") TempFile[] files) throws IOException {
		List<NutMap> data = Lists.newArrayList();
		for (TempFile tempFile : files) {
			NutMap temp = NutMap.NEW();
			String key = uploader.upload(Streams.readBytes(tempFile.getInputStream()));
			temp.put("key", key);
			temp.put("url", uploader.privateUrl(key));
			temp.put("name", tempFile.getName());
			temp.put("localName", tempFile.getSubmittedFileName());
			temp.put("size", tempFile.getSize());
			temp.put("type", tempFile.getContentType());
			data.add(temp);
		}
		return Result.success().addData("data", data);
	}

}

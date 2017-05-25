package club.zhcs.hammer.biz;

import java.io.InputStream;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

@IocBean(create = "init")
public class QiniuUploader {

	@Inject
	PropertiesProxy config;

	UploadManager uploadManager = new UploadManager(new Configuration(Zone.zone0()));

	String upToken;

	Log log = Logs.get();

	public void init() {
		Auth auth = Auth.create(config.get("qn.ak"), config.get("qn.sk"));
		upToken = auth.uploadToken(config.get("qn.bucket"));
	}

	public static class R {

		private String key;

		private String domain;

		public R(String domain, String key) {
			super();
			this.domain = domain;
			this.key = key;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return String.format("%s%s", domain, key);
		}

	}

	/**
	 * 上传本地文件
	 * 
	 * @param path
	 *            路径
	 * @return R
	 */
	public R upload(String path) {
		try {
			Response response = uploadManager.put(path, null, upToken);
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			return new R(config.get("qn.domain"), putRet.key);
		} catch (QiniuException e) {
			log.debugf("QiniuException: %s", e.getMessage());
			return null;
		}
	}

	/**
	 * 上传数据
	 * 
	 * @param data
	 *            数据
	 * @return R
	 */
	public R upload(byte[] data) {
		try {
			Response response = uploadManager.put(data, null, upToken);
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			return new R(config.get("qn.domain"), putRet.key);
		} catch (QiniuException e) {
			log.debugf("QiniuException: %s", e.getMessage());
			return null;
		}
	}

	/**
	 * 上传流
	 * 
	 * @param stream
	 *            流
	 * @return R
	 */
	public R upload(InputStream stream) {
		try {
			Response response = uploadManager.put(stream, null, upToken, null, null);
			DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
			return new R(config.get("qn.domain"), putRet.key);
		} catch (QiniuException e) {
			log.debugf("QiniuException: %s", e.getMessage());
			return null;
		}
	}

}

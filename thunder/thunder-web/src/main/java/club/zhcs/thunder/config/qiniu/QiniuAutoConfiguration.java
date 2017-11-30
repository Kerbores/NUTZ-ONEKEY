package club.zhcs.thunder.config.qiniu;

import java.io.InputStream;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

/**
 * @author kerbores
 *
 */
@Configuration
@ConditionalOnClass(UploadManager.class)
@EnableConfigurationProperties(QiuniuConfigProperties.class)
public class QiniuAutoConfiguration {
	@Autowired
	private QiuniuConfigProperties qiuniuConfigProperties;

	Log log = Logs.get();

	@Bean(initMethod = "init")
	public QiniuUploader qiniuUploader() {
		return new QiniuUploader(qiuniuConfigProperties);
	}

	public static class QiniuUploader {

		Log log = Logs.get();

		UploadManager uploadManager = new UploadManager(new com.qiniu.storage.Configuration(Zone.zone0()));

		String upToken;

		private QiuniuConfigProperties qiuniuConfigProperties;

		/**
		 * @param qiuniuConfigProperties
		 */
		public QiniuUploader(QiuniuConfigProperties qiuniuConfigProperties) {
			super();
			this.qiuniuConfigProperties = qiuniuConfigProperties;
		}

		public void init() {
			Auth auth = Auth.create(qiuniuConfigProperties.getAccessKey(), qiuniuConfigProperties.getSecretKey());
			upToken = auth.uploadToken(qiuniuConfigProperties.getBucket());
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
				return new R(qiuniuConfigProperties.getDomain(), putRet.key);
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
				return new R(qiuniuConfigProperties.getDomain(), putRet.key);
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
				return new R(qiuniuConfigProperties.getDomain(), putRet.key);
			} catch (QiniuException e) {
				log.debugf("QiniuException: %s", e.getMessage());
				return null;
			}
		}

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
}

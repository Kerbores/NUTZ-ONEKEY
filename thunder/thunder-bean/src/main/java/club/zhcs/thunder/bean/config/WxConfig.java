package club.zhcs.thunder.bean.config;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_wechat_config")
@Comment("微信配置")
public class WxConfig extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("wc_app_id")
	@Comment("微信appid")
	private String appid;

	@Column("wc_app_secret")
	@Comment("微信appsecret")
	private String appsecret;

	@Column("wc_token")
	@Comment("微信token")
	private String token;

	@Column("wc_encoding_aes_key")
	@Comment("微信encodingAesKey")
	private String encodingAesKey;

	/**
	 * @return the appid
	 */
	public String getAppid() {
		return appid;
	}

	/**
	 * @return the appsecret
	 */
	public String getAppsecret() {
		return appsecret;
	}

	/**
	 * @return the encodingAesKey
	 */
	public String getEncodingAesKey() {
		return encodingAesKey;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param appid
	 *            the appid to set
	 */
	public void setAppid(String appid) {
		this.appid = appid;
	}

	/**
	 * @param appsecret
	 *            the appsecret to set
	 */
	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	/**
	 * @param encodingAesKey
	 *            the encodingAesKey to set
	 */
	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}

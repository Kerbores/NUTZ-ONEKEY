package club.zhcs.thunder.bean.qa;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project thunder-bean
 *
 * @file Nutzer.java
 *
 * @description Nutz 用户
 *
 * @time 2016年9月5日 上午8:54:35
 *
 */
@Table("t_nutzer")
@Comment("nutz 用户")
public class Nutzer extends WxUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("n_access_token")
	@Comment("nutz.cn的accesstoken")
	private String accessToken;

	@Column("n_login_name")
	@Comment("登录名")
	private String loginName;

	@Column("n_score")
	@Comment("积分")
	private String score;

	@Column("n_avatar_url")
	@Comment("头像")
	@ColDefine(width=150)
	private String avatarUrl;

	@Column("n_create_at")
	@Comment("注册时间")
	private String create_at;

	@Column("n_create_at_forread")
	@Comment("注册相对时间")
	private String create_at_forread;

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return the loginName
	 */
	public String getLoginName() {
		return loginName;
	}

	/**
	 * @param loginName
	 *            the loginName to set
	 */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	/**
	 * @return the score
	 */
	public String getScore() {
		return score;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(String score) {
		this.score = score;
	}

	/**
	 * @return the avatarUrl
	 */
	public String getAvatarUrl() {
		return avatarUrl;
	}

	/**
	 * @param avatarUrl
	 *            the avatarUrl to set
	 */
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	/**
	 * @return the create_at
	 */
	public String getCreate_at() {
		return create_at;
	}

	/**
	 * @param create_at
	 *            the create_at to set
	 */
	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}

	/**
	 * @return the create_at_forread
	 */
	public String getCreate_at_forread() {
		return create_at_forread;
	}

	/**
	 * @param create_at_forread
	 *            the create_at_forread to set
	 */
	public void setCreate_at_forread(String create_at_forread) {
		this.create_at_forread = create_at_forread;
	}

}

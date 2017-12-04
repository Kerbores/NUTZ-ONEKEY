package club.zhcs.thunder.bean.log;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Times;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project bean
 *
 * @file LoginLog.java
 *
 * @description 登录日志
 *
 * @time 2016年3月15日 下午4:37:02
 *
 */
@Table("t_user_login")
public class LoginLog extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column("login_user_id")
	@Comment("登录用户 id")
	private long userId;

	@Column("login_account")
	@Comment("登录账户")
	private String account;

	@Column("login_time")
	@Comment("登录时间")
	private Date loginTime = Times.now();

	@Column("login_ip")
	@Comment("登录 ip")
	private String ip;

	public String getAccount() {
		return account;
	}

	public String getIp() {
		return ip;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public long getUserId() {
		return userId;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}

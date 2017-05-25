package club.zhcs.hammer.bean.log;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Times;

import club.zhcs.titans.utils.db.po.Entity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_user_login")
public class LoginLog extends Entity {

	@Column("login_user_id")
	@Comment("登录用户 id")
	private int userId;

	@Column("login_account")
	@Comment("登录账户")
	private String account;

	@Column("login_time")
	@Comment("登录时间")
	private Date loginTime = Times.now();

	@Column("login_ip")
	@Comment("登录 ip")
	private String ip;

	@Column("login_status")
	@Comment("登录成功与否")
	private boolean success;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getAccount() {
		return account;
	}

	public String getIp() {
		return ip;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public int getUserId() {
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

	public void setUserId(int userId) {
		this.userId = userId;
	}

}

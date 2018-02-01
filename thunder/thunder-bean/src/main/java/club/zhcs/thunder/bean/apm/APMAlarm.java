package club.zhcs.thunder.bean.apm;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;

import club.zhcs.thunder.bean.ThunderEntity;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
@Table("t_apm_alarm")
@Comment("性能告警表")
public class APMAlarm extends ThunderEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static enum Type {
		/**
		 * 
		 */
		SLOW, 
		/**
		 * 
		 */
		MEM, 
		/**
		 * 
		 */
		DISK, 
		/**
		 * 
		 */
		CPU, 
		/**
		 * 
		 */
		NETWORK;
	}

	@Name
	@Column("alarm_code")
	@Comment("报警信息编号")
	private String code = R.UU32();

	@Column("alarm_type")
	@Comment("报警类型")
	private Type type;

	@Column("alarm_time")
	@Comment("报警时间")
	private Date alarmTime = Times.now();

	@Column("alarm_msg")
	@Comment("报警消息")
	@ColDefine(width = 250)
	private String msg;

	@Column("alarm_ip")
	@Comment("报警 ip")
	private String ip;

	@Column("alarm_title")
	@Comment("报警标题")
	private String title;

	@Column("alarm_device")
	@Comment("报警设备")
	private String device;

	@Column("alarm_usage")
	@Comment(" 设备使用情况")
	private double usage;

	@Column("alarm_alarm_point")
	@Comment("设备告警点")
	private int alarm;

	public int getAlarm() {
		return alarm;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public String getCode() {
		return code;
	}

	public String getDevice() {
		return device;
	}

	public String getIp() {
		return ip;
	}

	public String getMsg() {
		return msg;
	}

	public String getTitle() {
		return title;
	}

	public Type getType() {
		return type;
	}

	public double getUsage() {
		return usage;
	}

	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setUsage(double usage) {
		this.usage = usage;
	}

}

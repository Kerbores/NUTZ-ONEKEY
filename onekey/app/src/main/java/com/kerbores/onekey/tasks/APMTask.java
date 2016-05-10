package com.kerbores.onekey.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.Sigar;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.bean.WxTemplateData;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kerbores.onekey.biz.acl.UserService;
import com.kerbores.onekey.biz.email.EmailService;
import com.kerbores.onkey.bean.acl.User;
import com.kerbores.onkey.bean.apm.APMAlarm;
import com.kerbores.onkey.bean.apm.APMAlarm.Type;
import com.kerbores.sigar.gathers.CPUGather;
import com.kerbores.sigar.gathers.MemoryGather;
import com.kerbores.utils.collection.Lists;
import com.kerbores.utils.common.Ips;
import com.kerbores.utils.common.Numbers;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project app
 *
 * @file APMTask.java
 *
 * @description // TODO write some description of this class
 *
 * @time 2016年3月15日 上午11:54:46
 *
 */
@IocBean(name = "apmTask", fields = "dao", create = "init")
public class APMTask implements Job {
	private static Log LOG = Logs.getLog(APMTask.class);
	private Dao dao;

	@Inject
	PropertiesProxy config;

	@Inject("wxApi")
	WxApi2 api;

	@Inject
	UserService userService;

	@Inject
	EmailService emailService;

	List<User> listeners = Lists.newArrayList();

	public void init() {
		String listener = config.get("alarm.listener");
		Lang.each(listener.split(","), new Each<String>() {

			@Override
			public void invoke(int index, String lis, int length) throws ExitLoop, ContinueLoop, LoopException {
				listeners.add(userService.fetch(Cnd.where("name", "=", lis)));
			}
		});
	}

	/**
	 * 
	 */
	public APMTask() {
	}

	public APMTask(Dao dao) {
		this.dao = dao;
	}

	public Dao getDao() {
		return dao;
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

	public String hostIp = Ips.hostIp();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Sigar sigar = new Sigar();
		MemoryGather memory = MemoryGather.init(sigar);
		// 内存
		double jvmUsage, ramUsage, swapUsage;
		if ((jvmUsage = memory.getJvm().getUsage()) > config.getInt("jvm.alarm.percent")) {
			alarm(Type.MEM, "内存警告", "JVM", jvmUsage, config.getInt("jvm.alarm.percent"));
		}
		if ((ramUsage = memory.getRam().getUsage()) > config.getInt("ram.alarm.percent")) {
			alarm(Type.MEM, "内存警告", "RAM", ramUsage, config.getInt("ram.alarm.percent"));
		}
		if ((swapUsage = memory.getSwap().getUsage()) > config.getInt("swap.alarm.percent")) {
			alarm(Type.MEM, "内存警告", "SWAP", swapUsage, config.getInt("swap.alarm.percent"));
		}

		CPUGather cpu = CPUGather.init(sigar);

		// CPU
		double cpuUsage;
		if ((cpuUsage = cpu.getTotal().getDouble("usage")) > config.getInt("cpu.alarm.percent")) {
			alarm(Type.MEM, "CPU警告", "CPU", cpuUsage, config.getInt("cpu.alarm.percent"));
		}

		// 磁盘

		// 网络
	}

	/**
	 * 
	 * @param type
	 * @param title
	 * @param device
	 * @param usage
	 * @param alarmPoint
	 */
	@Async
	private void alarm(Type type, String title, String device, double usage, int alarmPoint) {
		final APMAlarm alarm = new APMAlarm();
		alarm.setType(type);
		alarm.setIp(hostIp);
		alarm.setMsg(String.format("%s:当前 %s 使用率 %f,高于预警值 %d", title, device, usage, alarmPoint));
		alarm.setTitle(title);
		alarm.setDevice(device);
		alarm.setUsage(usage);
		alarm.setAlarm(alarmPoint);

		String alarmTypes = config.get(device.toLowerCase() + ".alarm.types");

		Lang.each(alarmTypes.split(","), new Each<String>() {

			@Override
			public void invoke(int index, String type, int length) throws ExitLoop, ContinueLoop, LoopException {
				if (Strings.equals(type, "EMAIL")) {// 发送邮件
					sendALarmByEmail(alarm);
				}
				if (Strings.equals(type, "SMS")) {// 发送短信

				}
				if (Strings.equals(type, "WECHAT")) {// 发送微信消息
					sendAlarmByWechat(alarm);
				}
			}

		});

		if (dao == null) {
			LOG.debug(alarm);
		} else {
			dao.insert(alarm);
		}
	}

	@Async
	private void sendALarmByEmail(final APMAlarm alarm) {

		Lang.each(listeners, new Each<User>() {

			@Override
			public void invoke(int index, User user, int length) throws ExitLoop, ContinueLoop, LoopException {
				if (user == null) {
					return;
				}
				emailService.sendAlarm(alarm, user.getEmail());
			}
		});

	}

	/**
	 * @param listener
	 * @param alarm
	 */
	@Async
	protected void sendAlarmByWechat(APMAlarm alarm) {
		final Map<String, WxTemplateData> data = new HashMap<String, WxTemplateData>();
		data.put("type", new WxTemplateData(alarm.getTitle()));
		data.put("ip", new WxTemplateData(alarm.getIp()));
		data.put("key", new WxTemplateData(alarm.getDevice()));
		data.put("usage", new WxTemplateData(Numbers.keepPrecision(alarm.getUsage() + "", 2)));
		data.put("alarm", new WxTemplateData(alarm.getAlarm() + ""));
		Lang.each(listeners, new Each<User>() {

			@Override
			public void invoke(int index, User user, int length) throws ExitLoop, ContinueLoop, LoopException {
				if (user == null) {
					return;
				}
				WxResp resp = api.template_send(user.getOpenid(), "MnNkTihmclGa4OAFelkMwAwxUiKu41hsn2l9fHxLRdA", null, data);
				LOG.debug(resp);
			}
		});
	}
}

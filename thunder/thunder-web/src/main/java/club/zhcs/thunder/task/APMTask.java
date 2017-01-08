package club.zhcs.thunder.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.quartz.annotation.Scheduled;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.weixin.bean.WxTemplateData;
import org.nutz.weixin.spi.WxApi2;
import org.nutz.weixin.spi.WxResp;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.common.collect.Lists;

import club.zhcs.thunder.bean.acl.User;
import club.zhcs.thunder.bean.apm.APMAlarm;
import club.zhcs.thunder.bean.apm.APMAlarm.Type;
import club.zhcs.thunder.biz.acl.UserService;
import club.zhcs.thunder.biz.eamil.EmailService;
import club.zhcs.titans.gather.CPUGather;
import club.zhcs.titans.gather.DISKGather;
import club.zhcs.titans.gather.MemoryGather;
import club.zhcs.titans.gather.NetInterfaceGather;
import club.zhcs.titans.utils.common.Ips;
import club.zhcs.titans.utils.common.Numbers;
import club.zhcs.titans.utils.db.Result;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project app
 *
 * @file APMTask.java
 *
 * @description 告警检测业务
 *
 * @time 2016年3月15日 上午11:54:46
 *
 */
@IocBean(name = "apmTask", fields = "dao", create = "init")
@Scheduled(cron = "*/10 * * * * ? ")
public class APMTask implements Job {
	private static Log LOG = Logs.getLog(APMTask.class);
	private Dao dao;

	/**
	 * 时间点
	 */
	private List<Date> timePoints = new ArrayList();

	private List<Double> cpuUsages = new ArrayList();

	private List<Double> ramUsages = new ArrayList();

	private List<Double> jvmUsages = new ArrayList();

	private List<Double> swapUsages = new ArrayList();

	private List<Double> niUsages = new ArrayList();

	private List<Double> noUsages = new ArrayList();

	private int monitorCount = 25;

	public Result data() {

		return Result.success().addData("timePoints", timePoints).addData("cpuUsages", cpuUsages)
				.addData("ramUsages", ramUsages).addData("jvmUsages", jvmUsages).addData("swapUsages", swapUsages)
				.addData("niUsages", niUsages).addData("noUsages", noUsages);
	}

	/**
	 * 添加数据
	 * 
	 * @param list
	 *            列表
	 * @param obj
	 *            待添加数据
	 */
	private void add(List list, Object obj) {
		if (obj instanceof Number) {
			list.add(Numbers.keepPrecision((Number) obj, 2));
		} else {
			list.add(obj);
		}
		if (list.size() > monitorCount) {
			list.remove(0);
		}
	}

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
		if (!config.getBoolean("install-flag")) {
			return;
		}
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
//		if (!config.getBoolean("install-flag")) {
//			return;
//		}
		try {
			Sigar sigar = new Sigar();
			MemoryGather memory = MemoryGather.gather(sigar);

			// 内存
			double jvmUsage, ramUsage, swapUsage = 0;
			if ((jvmUsage = memory.getJvm().getUsedPercent()) > config.getInt("jvm.alarm.percent")) {
				alarm(Type.MEM, "内存警告", "JVM", jvmUsage, config.getInt("jvm.alarm.percent"));
			}
			if ((ramUsage = memory.getMem().getUsedPercent()) > config.getInt("ram.alarm.percent")) {
				alarm(Type.MEM, "内存警告", "RAM", ramUsage, config.getInt("ram.alarm.percent"));
			}
			if (memory.getSwap().getTotal() != 0) {
				if ((swapUsage = memory.getSwap().getUsed() * 100 / memory.getSwap().getTotal()) > config
						.getInt("swap.alarm.percent")) {
					alarm(Type.MEM, "内存警告", "SWAP", swapUsage, config.getInt("swap.alarm.percent"));
				}
			}

			CPUGather cpu = CPUGather.gather(sigar);

			// CPU
			double cpuUsage;

			if ((cpuUsage = 100 - cpu.getPerc().getIdle() * 100) > config.getInt("cpu.alarm.percent")) {
				alarm(Type.MEM, "CPU警告", "CPU", cpuUsage, config.getInt("cpu.alarm.percent"));
			}
			// 磁盘

			List<DISKGather> disks = DISKGather.gather(sigar);
			for (DISKGather disk : disks) {
				if (disk.getStat() != null
						&& disk.getStat().getUsePercent() * 100 > config.getInt("disk.alarm.percent")) {
					alarm(Type.DISK, "磁盘警告", "DISK", disk.getStat().getUsePercent(),
							config.getInt("disk.alarm.percent"));
				}
			}

			// 网络流量
			double niUsage, noUsage;
			NetInterfaceGather ni = NetInterfaceGather.gather(sigar);
			if ((niUsage = ni.getRxbps() * 100 / ni.getStat().getSpeed()) > config.getInt("network.alarm.percent")) {
				alarm(Type.NETWORK, "流量警告", "NETWORK", niUsage, config.getInt("network.alarm.percent"));
			}
			if ((noUsage = ni.getTxbps() * 100 / ni.getStat().getSpeed()) > config.getInt("network.alarm.percent")) {
				alarm(Type.NETWORK, "流量警告", "NETWORK", noUsage, config.getInt("network.alarm.percent"));
			}
			add(timePoints, Times.format("HH:mm:ss", Times.now()));
			add(jvmUsages, jvmUsage);
			add(ramUsages, ramUsage);
			add(swapUsages, swapUsage);
			add(cpuUsages, cpuUsage);
			add(niUsages, niUsage);
			add(noUsages, noUsage);
		} catch (SigarException e) {
			LOG.error(e);
		}

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
				WxResp resp = api.template_send(user.getOpenid(), "MnNkTihmclGa4OAFelkMwAwxUiKu41hsn2l9fHxLRdA", null,
						data);
				LOG.debug(resp);
			}
		});
	}
}

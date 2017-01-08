package club.zhcs.thunder.aop;

import org.apache.shiro.SecurityUtils;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.aop.interceptor.async.Async;
import org.nutz.castor.Castors;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

import club.zhcs.thunder.bean.apm.APMAlarm;
import club.zhcs.thunder.bean.apm.APMAlarm.Type;
import club.zhcs.thunder.bean.log.OperationLog;
import club.zhcs.titans.utils.common.Ips;
import club.zhcs.titans.utils.db.Result;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project app
 *
 * @file APM.java
 *
 * @description // TODO write some description of this class
 *
 * @time 2016年3月15日 上午9:42:13
 *
 */
@IocBean(name = "apm", fields = "dao")
public class APM implements MethodInterceptor {

	private static Log LOG = Logs.getLog(APM.class);

	private Dao dao;

	public String hostIp = Ips.hostIp();

	/**
	 * 
	 */
	public APM() {
	}

	public APM(Dao dao) {
		this.dao = dao;
	}

	@Async
	public void alarm(Type type, String msg) {
		System.err.println(2);
		APMAlarm alarm = new APMAlarm();
		alarm.setType(type);
		alarm.setMsg(msg);
		alarm.setIp(hostIp);
		if (dao == null) {
			LOG.debug(alarm);
		} else {
			dao.insert(alarm);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.aop.MethodInterceptor#filter(org.nutz.aop.InterceptorChain)
	 */
	@Override
	public void filter(InterceptorChain chain) throws Throwable {

		SystemLog log = chain.getCallingMethod().getAnnotation(SystemLog.class);
		if (log == null) {
			LOG.debug("没有配置咯!");
			Stopwatch stopwatch = Stopwatch.begin();
			chain.doChain();
			stopwatch.stop();
			if (stopwatch.getDuration() > 5000) {
				alarm(Type.SLOW, "慢响应警告: 当前请求耗时 " + stopwatch.getDuration() + " 毫秒,高于预警值 5000 毫秒");
			}
			return;
		}

		String ip = Lang.getIP(Mvcs.getReq());

		String user = SecurityUtils.getSubject().getPrincipal().toString();

		OperationLog operationLog = new OperationLog();

		operationLog.setAccount(user);
		operationLog.setAction(log.methods());
		operationLog.setIp(ip);
		operationLog.setModule(log.module());
		

		Stopwatch stopwatch = Stopwatch.begin();
		chain.doChain();
		stopwatch.stop();

		Object obj = chain.getReturn();

		if (obj instanceof Result) {
			operationLog.setDescription(Castors.me().castTo(obj, Result.class).isSuccess() ? "操作成功" : "操作失败");
		}
		if (Strings.isBlank(operationLog.getDescription())) {
			operationLog.setDescription(log.description());
		}

		operationLog.setOperationTime(stopwatch.getDuration());
		save(operationLog);
		System.err.println(1);
	}

	public Dao getDao() {
		return dao;
	}

	@Async
	public void save(OperationLog log) throws InterruptedException {
		if (dao == null) {
			LOG.debug(log);
		} else {
			dao.insert(log);
		}
	}

	public void setDao(Dao dao) {
		this.dao = dao;
	}

}

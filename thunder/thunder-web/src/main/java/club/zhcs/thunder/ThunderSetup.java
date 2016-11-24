package club.zhcs.thunder;

import java.nio.charset.Charset;

import org.apache.log4j.PropertyConfigurator;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Files;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import club.zhcs.thunder.bean.acl.Role;
import net.sf.ehcache.CacheManager;

public class ThunderSetup implements Setup {
	private static final Log log = Logs.get();


	Role admin;

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see org.nutz.mvc.Setup#destroy(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void destroy(NutConfig nc) {

	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * 
	 * 
	 * @see org.nutz.mvc.Setup#init(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void init(NutConfig nc) {

		Ioc ioc = nc.getIoc();

		final PropertiesProxy p = ioc.get(PropertiesProxy.class, "config");
		nc.setAttribute("rs", p.get("app-rs", ""));
		nc.setAttribute("appnm", p.get("app-name", "Thunder"));

		String logConfigPath = "/var/config/log4j.properties";
		try {
			if (Files.checkFile(logConfigPath) != null) {// 找到了线上配置
				PropertyConfigurator.configure(new PropertiesProxy(logConfigPath).toProperties());// 那么加载线上的配置吧!!!
			}
		} catch (Exception e) {
		}
		NutShiro.DefaultLoginURL = "/";
		NutShiro.DefaultNoAuthURL = "/403";

		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}


		CacheManager cacheManager = ioc.get(CacheManager.class);
		log.debug("Ehcache CacheManager = " + cacheManager);

		ioc.get(NutQuartzCronJobFactory.class);// 触发任务
		

		// ioc.get(SigarClient.class);// 触发 sigar


		
	}

}

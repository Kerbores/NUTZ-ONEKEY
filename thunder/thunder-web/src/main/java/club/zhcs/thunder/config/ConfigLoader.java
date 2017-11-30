package club.zhcs.thunder.config;

import javax.annotation.PostConstruct;

import org.nutz.ioc.impl.PropertiesProxy;
import org.springframework.stereotype.Service;

/**
 * @author kerbores
 *
 */
@Service
public class ConfigLoader extends PropertiesProxy {

	@PostConstruct
	public void init() {
		this.setIgnoreResourceNotFound(true);
		this.setPaths("application.properties", "config.properties", System.getProperty("sino.config.location"));
	}

}

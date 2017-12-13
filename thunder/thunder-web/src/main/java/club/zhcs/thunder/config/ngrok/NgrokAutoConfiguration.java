package club.zhcs.thunder.config.ngrok;

import org.nutz.plugins.ngrok.client.NgrokClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author kerbores@gmail.com
 *
 */
@Configuration
@ConditionalOnClass(NgrokClient.class)
@EnableConfigurationProperties(NgrokConfigProperties.class)
public class NgrokAutoConfiguration {

	@Autowired
	NgrokConfigProperties ngrokConfigProperties;

	@Value("${server.port}")
	int port;

	@Bean(initMethod = "start", destroyMethod = "stop")
	@ConditionalOnProperty(name = "ngrok.token")
	public NgrokClient ngrokClient() {
		NgrokClient client = new NgrokClient();
		client.to_port = ngrokConfigProperties.getPort() == 0 ? (port == 0 ? 8080 : port) : ngrokConfigProperties.getPort();
		client.auth_token = ngrokConfigProperties.getToken();
		return client;
	}

}

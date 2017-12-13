package club.zhcs.thunder.config.ngrok;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author kerbores@gmail.com
 *
 */
@ConfigurationProperties(prefix = "ngrok")
public class NgrokConfigProperties {

	String token;

	int port;

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}

}

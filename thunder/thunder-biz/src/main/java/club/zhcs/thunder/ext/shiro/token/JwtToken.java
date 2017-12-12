package club.zhcs.thunder.ext.shiro.token;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class JwtToken implements AuthenticationToken {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户名
	 */
	String principal;

	/**
	 * jwt token
	 */
	String credentials;

	/**
	 * 
	 */
	JwtToken() {
	}

	public static JwtToken me() {
		return new JwtToken();
	}

	public JwtToken principal(String principal) {
		this.principal = principal;
		return this;
	}

	public JwtToken token(String token) {
		this.credentials = token;
		return this;
	}

	/**
	 * @param principal
	 * @param credentials
	 */
	public JwtToken(String principal, String credentials) {
		super();
		this.principal = principal;
		this.credentials = credentials;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.shiro.authc.AuthenticationToken#getPrincipal()
	 */
	@Override
	public Object getPrincipal() {
		return principal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.shiro.authc.AuthenticationToken#getCredentials()
	 */
	@Override
	public Object getCredentials() {
		return credentials;
	}

}

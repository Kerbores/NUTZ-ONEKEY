package club.zhcs.thunder.ext.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.nutz.lang.Lang;

/**
 * 
 * @author kerbores@gmail.com
 *
 */
public class SINOCredentialsMatcher extends SimpleCredentialsMatcher {

	@Override
	public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		return equals(password(token.getPassword(), token.getUsername()), getCredentials(info));
	}

	/**
	 * 加密
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return 密码密文
	 */
	public static String password(char[] password, String userName) {
		return password(userName, new String(password));
	}

	/**
	 * 加密
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return 密码密文
	 */
	public static String password(String userName, String password) {
		return password(password.getBytes(), userName.getBytes());
	}

	/**
	 * 
	 * @param p
	 *            密码
	 * @param salt
	 *            盐
	 * @return 密码密文
	 */
	public static String password(byte[] p, byte[] salt) {
		return Lang.digest("MD5", p, salt, 2);
	}

}

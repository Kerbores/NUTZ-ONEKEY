package club.zhcs.hammer.ext.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * @author kerbores kerbores@gmail.com
 *
 */
public class ThunderCredentialsMatcher extends SimpleCredentialsMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.shiro.authc.credential.SimpleCredentialsMatcher#doCredentialsMatch(org.apache.shiro.authc.AuthenticationToken, org.apache.shiro.authc.AuthenticationInfo)
	 */
	@Override
	public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		Object tokenCredentials = new Md5Hash(new String(token.getPassword()), token.getUsername(), 2).toString();
		Object accountCredentials = getCredentials(info);
		return equals(tokenCredentials, accountCredentials);
	}

}

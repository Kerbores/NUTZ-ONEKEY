package club.zhcs.nail.biz;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import club.zhcs.nail.bean.acl.User;
import club.zhcs.nail.biz.shiro.ShiroUserService;

@Component
public class ThunderRealm extends AuthorizingRealm {

	@Autowired
	ShiroUserService shiroUserService;

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String userName = upToken.getUsername();
		User user = shiroUserService.findByName(userName);
		if (user == null) {
			throw new UnknownAccountException("不存在的用户!");
		}
		if (!user.isAvailable()) {
			throw new LockedAccountException("账户已锁定!");
		}
		return new SimpleAuthenticationInfo(userName,
				user.getPassword(),
				getName());
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String userName = principalCollection.getPrimaryPrincipal().toString();
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		List<String> roleNameList = shiroUserService.roleInfos(userName);
		auth.addRoles(roleNameList);// 添加角色
		List<String> permissionNames = shiroUserService.permissionInfos(userName);
		auth.addStringPermissions(permissionNames);// 添加权限
		return auth;
	}

}

package club.zhcs.thunder.config.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.nutz.lang.Lang;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import club.zhcs.thunder.ext.shiro.SINOAdvisor;
import club.zhcs.thunder.ext.shiro.matcher.SINOCredentialsMatcher;
import club.zhcs.thunder.ext.shiro.realm.SINORealm;

@Configuration
public class ShiroConfiguration {

	private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

	@Bean
	public SINOCredentialsMatcher sinoCredentialsMatcher() {
		return new SINOCredentialsMatcher();
	}


	@Bean
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	@ConditionalOnBean({ SINORealm.class, CredentialsMatcher.class })
	public DefaultWebSecurityManager getDefaultWebSecurityManager( SINORealm afdiRealm,
			CredentialsMatcher credentialsMatcher) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		afdiRealm.setCredentialsMatcher(credentialsMatcher);

		dwsm.setRealms(Lang.list(afdiRealm));
		
		DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator defaultSessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        defaultSessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(defaultSessionStorageEvaluator);
        dwsm.setSubjectDAO(subjectDAO);
        
		return dwsm;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	@ConditionalOnBean(SecurityManager.class)
	public Advisor getAuthorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		SINOAdvisor aasa = new SINOAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	@Bean(name = "shiroFilter")
	@ConditionalOnBean(SecurityManager.class)
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean
				.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl("/user/login");
		filterChainDefinitionMap.put("/captcha", "anon");
		filterChainDefinitionMap.put("/api-docs", "anon");
		filterChainDefinitionMap.put("/captcha", "anon");
		filterChainDefinitionMap.put("/v2/api-docs", "anon");
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		filterChainDefinitionMap.put("/webjars/**", "anon");
		filterChainDefinitionMap.put("/swagger-resources/**", "anon");

		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}

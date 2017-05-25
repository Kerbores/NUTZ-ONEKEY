package club.zhcs.nail.config.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import club.zhcs.nail.biz.ThunderRealm;
import club.zhcs.nail.shiro.MD5PasswordMatcher;
import club.zhcs.nail.shiro.ThunderAdvisor;

@Configuration
public class ShiroConfiguration {
	private static Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();

	@Bean
	public CredentialsMatcher credentialsMatcher() {
		return new MD5PasswordMatcher();
	}

	@Bean
	public EhCacheManager getEhCacheManager() {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManagerConfigFile("classpath:ehcache.xml");
		return em;
	}

	@Bean
	public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	@ConditionalOnBean({ CacheManager.class, ThunderRealm.class, CredentialsMatcher.class })
	public DefaultWebSecurityManager getDefaultWebSecurityManager(CacheManager cacheManager, ThunderRealm afdiRealm, CredentialsMatcher credentialsMatcher) {
		DefaultWebSecurityManager dwsm = new DefaultWebSecurityManager();
		afdiRealm.setCredentialsMatcher(credentialsMatcher);
		dwsm.setRealm(afdiRealm);
		dwsm.setCacheManager(cacheManager);
		return dwsm;
	}

	@Bean
	public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
		daap.setProxyTargetClass(true);
		return daap;
	}

	@Bean
	@ConditionalOnBean(org.apache.shiro.mgt.SecurityManager.class)
	public Advisor getAuthorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		ThunderAdvisor aasa = new ThunderAdvisor();
		aasa.setSecurityManager(securityManager);
		return aasa;
	}

	@Bean(name = "shiroFilter")
	@ConditionalOnBean(org.apache.shiro.mgt.SecurityManager.class)
	public ShiroFilterFactoryBean getShiroFilterFactoryBean(SecurityManager securityManager) {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean
				.setSecurityManager(securityManager);
		shiroFilterFactoryBean.setLoginUrl("/login");
		filterChainDefinitionMap.put("/swagger-ui.html", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
		return shiroFilterFactoryBean;
	}
}

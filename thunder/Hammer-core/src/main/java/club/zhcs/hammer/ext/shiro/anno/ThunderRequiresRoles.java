package club.zhcs.hammer.ext.shiro.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.annotation.Logical;

import club.zhcs.hammer.vo.InstalledRole;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description //TODO description
 * 
 * @copyright copyright©2016 zhcs.club
 *
 * @createTime 2016年8月23日 下午1:00:27
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ThunderRequiresRoles {
	Logical logical() default Logical.AND;

	InstalledRole[] value();
}

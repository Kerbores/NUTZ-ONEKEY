package club.zhcs.hammer.apm;  
  
import java.lang.annotation.*;  
  
/** 
 *自定义注解 拦截Controller 
 */  
  
@Target({ElementType.PARAMETER, ElementType.METHOD})  
@Retention(RetentionPolicy.RUNTIME)  
@Documented  
public  @interface SystemLog {  
  
	String description()  default "";  //
	String method()  default "";  //新增用户
    String module()  default "";  //模块名称 系统管理-用户管理－列表页面
  
  
}  
  

package club.zhcs.thunder.hanlder;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthenticatedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import club.zhcs.common.Result;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {
	Logger logger = Logger.getLogger(getClass());

	@ExceptionHandler(value = Exception.class)
	public Result defaultErrorHandler(HttpServletResponse response, Exception e) throws Exception {
		logger.error("error=>", e);
		if (e instanceof UnauthenticatedException) {
			response.setStatus(401);
			return Result.fail("没有权限进行此操作!");
		}
		return Result.exception(e);
	}
}

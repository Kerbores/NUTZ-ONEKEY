//package club.zhcs.thunder.ext.shiro.filter;
//
//import java.util.Date;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.shiro.authc.AuthenticationException;
//import org.apache.shiro.authc.AuthenticationToken;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
//import org.nutz.lang.Strings;
//import org.nutz.log.Logs;
//
//import club.zhcs.thunder.ext.shiro.token.JwtToken;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
///**
// * @author kerbores@gmail.com
// *
// */
//public class JwtAuthenticationFilter extends AuthenticatingFilter {
//
//	private static final String TOKEN = "Authorization";
//
//	private static final String KEY = "kerbores";
//	
//	
//	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.apache.shiro.web.filter.authc.AuthenticatingFilter#createToken(javax.
//	 * servlet.ServletRequest, javax.servlet.ServletResponse)
//	 */
//	@Override
//	protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
//		HttpServletRequest httpRequest = (HttpServletRequest) request;
//		String token = httpRequest.getHeader(TOKEN);
//		if (Strings.isBlank(token)) {
//			token = httpRequest.getParameter(TOKEN);
//		}
//		if (Strings.isBlank(token)) {
//			for (Cookie ele : httpRequest.getCookies()) {
//				if (TOKEN.equals(ele.getName())) {
//					token = ele.getValue();
//					break;
//				}
//			}
//		}
//		String user = null;
//		if (Strings.isNotBlank(token)) {
//			try {
//				user = Jwts.parser()
//						.setSigningKey(KEY)
//						.parseClaimsJws(token)
//						.getBody()
//						.getSubject();
//			} catch (Exception e) {
//				Logs.get().debugf("jwt error==> %s", e.getMessage());
//			}
//		}
//		return JwtToken.me().token(token).principal(user);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.apache.shiro.web.filter.AccessControlFilter#onAccessDenied(javax.servlet.
//	 * ServletRequest, javax.servlet.ServletResponse)
//	 */
//	@Override
//	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
//		return executeLogin(request, response);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.apache.shiro.web.filter.authc.AuthenticatingFilter#onLoginFailure(org.
//	 * apache.shiro.authc.AuthenticationToken,
//	 * org.apache.shiro.authc.AuthenticationException, javax.servlet.ServletRequest,
//	 * javax.servlet.ServletResponse)
//	 */
//	@Override
//	protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
//		return true;
//	}
//	
//	
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.apache.shiro.web.filter.authc.AuthenticatingFilter#onLoginSuccess(org.
//	 * apache.shiro.authc.AuthenticationToken, org.apache.shiro.subject.Subject,
//	 * javax.servlet.ServletRequest, javax.servlet.ServletResponse)
//	 */
//	@Override
//	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
//		HttpServletResponse resp = (HttpServletResponse) response;
//		String jwtToken = Jwts.builder()
//				.setSubject(Strings.safeToString(subject.getPrincipal(), ""))
//				.setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000))// 1天
//				.signWith(SignatureAlgorithm.HS512, KEY)
//				.compact();
//		// 添加cookie
//		Cookie cookie = new Cookie(TOKEN, jwtToken);
//		cookie.setHttpOnly(true);
//		cookie.setMaxAge(3600 * 5);
//		cookie.setPath("/");
//		resp.addCookie(cookie);
//		resp.flushBuffer();
//
//		// 添加到header
//		resp.addHeader(TOKEN, jwtToken);
//		return true;
//	}
//
//}

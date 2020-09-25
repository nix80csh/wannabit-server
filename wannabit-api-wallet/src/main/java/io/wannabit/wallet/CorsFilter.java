package io.wannabit.wallet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

  // prod, dev
  @Value("${spring.profiles.active}") private String deployType;

  @Override
  public void destroy() {}

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    if (deployType.equals("prod")) {
      response.setHeader("Access-Control-Allow-Origin", "https://wallet.wannabit.io");
    } else {
      response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
    }
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept, X-Auth-Token");

    System.out.println("IP Address >> " + getRemoteIP(request));

    // allow cros preflight
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      chain.doFilter(request, response);
    }
  }

  private static String getRemoteIP(HttpServletRequest request) {
    String ip = request.getHeader("X-FORWARDED-FOR");

    // proxy 환경일 경우
    if (ip == null || ip.length() == 0) {
      ip = request.getHeader("Proxy-Client-IP");
    }

    // 웹로직 서버일 경우
    if (ip == null || ip.length() == 0) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ip == null || ip.length() == 0) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {}
}

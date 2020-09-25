package io.wannabit.wallet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.wannabit.core.entity.Account;
import io.wannabit.core.repository.AccountRepo;
import io.wannabit.util.TokenUtil;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

  @Autowired AccountRepo accountRepo;
  @Value("${wannabit.token.header}") private String tokenHeader;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    String authToken = httpRequest.getHeader(this.tokenHeader);
    String email = TokenUtil.getUserIdFromToken(authToken);
    System.out.println("<필터진입> 아이디 : " + email);
    System.out.println("<필터진입> 인증토큰 : " + authToken);

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      Account account = this.accountRepo.findByEmail(email);
      if (account != null) {
        if (TokenUtil.validateToken(authToken, account.getEmail(), account.getPassword())) {
          UsernamePasswordAuthenticationToken authentication =
              new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    chain.doFilter(request, response);
  }
}

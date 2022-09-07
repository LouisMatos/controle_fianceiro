package br.com.controlefinanceiro.interceptor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import br.com.controlefinanceiro.model.Access;
import br.com.controlefinanceiro.model.User;

@SuppressWarnings("deprecation")
public class AccessInterceptor extends HandlerInterceptorAdapter {
	private static final Logger log = LogManager.getLogger(AccessInterceptor.class);

	private final List<Access> hits = new ArrayList<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Access access = new Access();

		access.setPath(request.getRequestURI());
		access.setDate(LocalDateTime.now());

		try {

			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			access.setName(user.getNome());
			access.setId(user.getId());
			access.setEmail(user.getEmail());

		} catch (Exception e) {
			log.warn("Usuário não logado!");
		}

		request.setAttribute("hits", access);

		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		Access access = (Access) request.getAttribute("hits");
		access.setDuration(Duration.between(access.getDate(), LocalDateTime.now()));
		hits.add(access);
		log.info("Logs de acessos: {}", access);
	}
}

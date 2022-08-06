package br.com.controlefinanceiro.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import br.com.controlefinanceiro.model.User;
import br.com.controlefinanceiro.util.Utils;

@Component
public class AuthenticationProviderService implements AuthenticationProvider{
	
	
	@Autowired
	private UserService usuarioService;

	@Override
	@SuppressWarnings("unchecked")
	public Authentication authenticate(Authentication auth) throws AuthenticationException {
		String login = auth.getName();
		String senha = auth.getCredentials().toString();

		Optional<User> usuarioBd = usuarioService.buscarUser(login);

		if (usuarioBd.isPresent()) {
			if (usuarioAtivo(usuarioBd.get()) && Utils.verificaSenha(senha, usuarioBd.get().getSenha())) {

				Collection<? extends GrantedAuthority> authorities = usuarioBd.get().getPapeis();

				return new UsernamePasswordAuthenticationToken(usuarioBd.get(), senha, authorities);

			} else {
				throw new UsernameNotFoundException("Login e/ou Senha inválidos.");
			}
		}
		throw new UsernameNotFoundException("Login e/ou Senha inválidos.");
	}

	@Override
	public boolean supports(Class<?> auth) {
		return auth.equals(UsernamePasswordAuthenticationToken.class);
	}

	private boolean usuarioAtivo(User usuario) {
		if (usuario != null) {
			if (usuario.getStatus() == true) {
				return true;
			}
		}
		throw new BadCredentialsException("Este usuário está desativado.");
	}

}

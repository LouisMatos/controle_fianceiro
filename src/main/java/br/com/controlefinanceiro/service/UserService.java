package br.com.controlefinanceiro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.controlefinanceiro.enums.EnumModule;
import br.com.controlefinanceiro.model.Paper;
import br.com.controlefinanceiro.model.User;
import br.com.controlefinanceiro.model.UserDTO;
import br.com.controlefinanceiro.model.RegisteredUsersDTO;
import br.com.controlefinanceiro.repository.UserRepository;
import br.com.controlefinanceiro.util.EnviarEmail;
import br.com.controlefinanceiro.util.Utils;

@Service
public class UserService {

	@Autowired
	private UserRepository usuarioRepository;

	@Autowired
	private EnviarEmail sendEmail;

	public List<RegisteredUsersDTO> buscarUsersCadastrados() {
		return usuarioRepository.findUsuariosCadastrados();
	}

	public boolean verificaEmailExiste(UserDTO usuarioDTO) {
		if (usuarioRepository.findByEmail(usuarioDTO.getEmail().toUpperCase()).isPresent()) {
			return true;
		}
		return false;
	}

	public void criarNovoUser(@Valid UserDTO usuarioDTO) {

		usuarioDTO.setSenha(Utils.getRandomNumberString());

		User usuario = new User();
		usuario.setEmail(usuarioDTO.getEmail().toUpperCase());
		usuario.setNome(usuarioDTO.getNome());
		List<Paper> papeis = new ArrayList<Paper>();
		Paper papel = new Paper();
		papel.setNome(EnumModule.USUARIO);
		papeis.add(papel);
		usuario.setPapeis(papeis);
		usuario.setCadastro(LocalDateTime.now());
		usuario.setStatus(true);
		usuario.setSenha(Utils.encrypt(usuarioDTO.getSenha()));

		sendEmail.enviarEmail(usuarioDTO);
		usuarioRepository.save(usuario).getId();

	}

	public Optional<User> buscarUser(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public Optional<User> buscarUserPorId(Integer id) {
		return usuarioRepository.findById(id);
	}

	public Optional<User> verificaEmailESenha(String login, String senha) {
		return usuarioRepository.findByEmailAndSenha(login, senha);
	}

	public boolean excluirUserDoSistema(Integer id) {
		Optional<User> usuario = usuarioRepository.findById(id);

		if (usuario.isPresent()) {
			User usuarioBD = usuario.get();
			usuarioBD.setStatus(false);
			usuarioRepository.save(usuarioBD);
			return true;
		} else {
			return false;
		}

	}

	public boolean verificaUserAdministradorSistema(Integer id) {
		Optional<User> usuario = usuarioRepository.findById(id);
		if (usuario.get().getNome().equalsIgnoreCase("Admin")
				&& usuario.get().getEmail().equalsIgnoreCase("admin@email.com.br")) {
			return true;
		}
		return false;
	}

	public void editarUser(@Valid UserDTO usuarioDTO) {
		User usuario = usuarioRepository.findById(usuarioDTO.getId()).get();

		usuario.setEmail(usuarioDTO.getEmail().toUpperCase());
		usuario.setNome(usuarioDTO.getNome());

		if (usuarioDTO.isSenhaNova()) {
			usuario.setSenha(Utils.encrypt(Utils.getRandomNumberString()));
		}

		usuarioRepository.save(usuario);
		sendEmail.enviarEmail(usuarioDTO);
	}

}

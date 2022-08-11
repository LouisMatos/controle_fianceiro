package br.com.controlefinanceiro.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static Logger Log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EnviarEmail sendEmail;

	public List<RegisteredUsersDTO> searchForRegisteredUsers() {
		List<RegisteredUsersDTO> user =  userRepository.findRegisteredUsers();
		
		Log.info(user.toString());
		return user;
	}

	public boolean emailAlreadyRegistered(UserDTO userDTO) {
		if (userRepository.findByEmail(userDTO.getEmail().toUpperCase()).isPresent()) {
			return true;
		}
		return false;
	}

	public void createNewUser(@Valid UserDTO userDTO) {

		userDTO.setSenha(Utils.getRandomNumberString());

		User usuario = new User();
		usuario.setEmail(userDTO.getEmail().toUpperCase());
		usuario.setNome(userDTO.getNome());
		List<Paper> papeis = new ArrayList<Paper>();
		Paper papel = new Paper();
		papel.setNome(EnumModule.USUARIO);
		papeis.add(papel);
		usuario.setPapeis(papeis);
		usuario.setCadastro(LocalDateTime.now());
		usuario.setStatus(true);
		usuario.setSenha(Utils.encrypt(userDTO.getSenha()));

		//sendEmail.enviarEmail(userDTO);
		userRepository.save(usuario).getId();

	}

	public Optional<User> buscarUser(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> buscarUserPorId(Integer id) {
		return userRepository.findById(id);
	}

	public Optional<User> verificaEmailESenha(String login, String senha) {
		return userRepository.findByEmailAndSenha(login, senha);
	}

	public boolean excluirUserDoSistema(Integer id) {
		Optional<User> usuario = userRepository.findById(id);

		if (usuario.isPresent()) {
			User usuarioBD = usuario.get();
			usuarioBD.setStatus(false);
			userRepository.save(usuarioBD);
			return true;
		} else {
			return false;
		}

	}

	public boolean verificaUserAdministradorSistema(Integer id) {
		Optional<User> usuario = userRepository.findById(id);
		if (usuario.get().getNome().equalsIgnoreCase("Admin")
				&& usuario.get().getEmail().equalsIgnoreCase("admin@email.com.br")) {
			return true;
		}
		return false;
	}

	public void editarUser(@Valid UserDTO usuarioDTO) {
		User usuario = userRepository.findById(usuarioDTO.getId()).get();

		usuario.setEmail(usuarioDTO.getEmail().toUpperCase());
		usuario.setNome(usuarioDTO.getNome());

		if (usuarioDTO.isSenhaNova()) {
			usuario.setSenha(Utils.encrypt(Utils.getRandomNumberString()));
		}

		userRepository.save(usuario);
		sendEmail.enviarEmail(usuarioDTO);
	}

}

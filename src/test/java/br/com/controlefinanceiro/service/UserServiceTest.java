package br.com.controlefinanceiro.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import br.com.controlefinanceiro.model.RegisteredUsersDTO;
import br.com.controlefinanceiro.model.UserDTO;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

	@Autowired
	private UserService service;

	private UserDTO userDTO;

	private Random r;

	@BeforeEach
	public void inicializar() {
		createNewUserForTest();
	}

	private void createNewUserForTest() {
		r = new Random();

		this.userDTO = UserDTO.builder() //
				.nome("Luis Test") //
				.email("luis" + r.nextInt(9999) + "@teste.com.br") //
				.senha("123999") //
				.build();

		service.createNewUser(userDTO);
	}

	@Test
	void validateEmptySearchForRegisteredUsers() {
		List<RegisteredUsersDTO> users = service.searchForRegisteredUsers();
		assertFalse(users.isEmpty());
	}

	@Test
	void validateSearchForRegisteredUsers() {
		List<RegisteredUsersDTO> users = service.searchForRegisteredUsers();
		assertFalse(users.isEmpty());
	}

	@Test
	void validateEmailAlreadyRegistrered() {
		boolean AlreadyRegistrered = service.emailAlreadyRegistered(this.userDTO);
		assertTrue(AlreadyRegistrered);
	}

	@Test
	void validateCreateNewUser() {
		assertTrue(true);
	}

}

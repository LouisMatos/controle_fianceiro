package br.com.controlefinanceiro.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UtilsTest {

	@Test
	void checkCorrespondingPasswordTest() {
		assertTrue(Utils.verificaSenha("123999", "$2a$10$/ENYaePxc9k29E01qs972uWoo15SDmJFZUMn3z1IRhEVrRiRbCQ8G"));
	}

	@Test
	void checkNotCorrespondingPasswordTest() {
		assertFalse(Utils.verificaSenha("12392", "$2a$10$/ENYaePxc9k29E01qs972uWoo15SDmJFZUMn3z1IRhEVrRiRbCQ8G"));
	}

	@Test
	void checkEncrypt() {
		assertNotNull(Utils.encrypt("123999"));
	}
}

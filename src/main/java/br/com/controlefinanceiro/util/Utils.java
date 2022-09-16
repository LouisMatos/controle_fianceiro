package br.com.controlefinanceiro.util;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Utils {

	private static Logger log = LoggerFactory.getLogger(Utils.class);

	private static BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
	private static final Random rnd = new Random();

	public static String getRandomNumberString() {
		int number = rnd.nextInt(999999);
		return String.format("%06d", number);
	}

	public static String encrypt(String encrypt) {
		return bc.encode(encrypt);
	}

	public static boolean verificaSenha(String senha, String senhaEncrypt) {
		return bc.matches(senha, senhaEncrypt);
	}

	public static void main(String[] args) {
		String encrypt = encrypt("123999");
		log.info("senha encrypt: {}", encrypt);
	}

}

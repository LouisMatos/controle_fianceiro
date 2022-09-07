package br.com.controlefinanceiro.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.controlefinanceiro.model.UserDTO;

@Component
public class EnviarEmail {

	private static final Logger log = LogManager.getLogger(EnviarEmail.class);

	@Value("${send.smtp.email}")
	private String emailDe;

	@Value("${send.smtp.senha}")
	private String senha;

	@Value("${send.smtp.host}")
	private String host;

	@Value("${send.smtp.port}")
	private String port;

	@Value("${send.smtp.ssl}")
	private String ssl;

	@Value("${send.smtp.auth}")
	private String auth;

	public void enviarEmail(UserDTO usuarioDTO) {
		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.ssl.enable", ssl);
		properties.put("mail.smtp.auth", auth);

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailDe, senha);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);

			message.setFrom(new InternetAddress(emailDe));

			message.addRecipient(Message.RecipientType.TO, new InternetAddress(usuarioDTO.getEmail()));

			message.setSubject("Acesso do " + usuarioDTO.getNome() + " ao sistema de controle Finaceiro");

			message.setText("Nome: " + usuarioDTO.getNome() + "\n" + "Email: " + usuarioDTO.getEmail() + "\n"
					+ "Senha: " + usuarioDTO.getSenha());

			log.info("enviando ...");
			Transport.send(message);
			log.info("Mensagem enviada com sucesso ....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}

	}

}

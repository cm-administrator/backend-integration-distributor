package com.br.distributors.service;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.br.distributors.models.EmailConfiguration;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

	public void send(EmailConfiguration config, String to, String subject, String content, boolean isHtml)
			throws MessagingException {

		JavaMailSenderImpl mailSender = buildSender(config);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

		setFrom(message, config);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, isHtml);

		mailSender.send(message);
	}

	public void sendWithAttachment(EmailConfiguration config, String to, String subject, String content,
			byte[] fileBytes, String filename, boolean isHtml) throws MessagingException {

		JavaMailSenderImpl mailSender = buildSender(config);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

		setFrom(message, config);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, isHtml);

		InputStreamSource attachment = new ByteArrayResource(fileBytes);
		helper.addAttachment(filename, attachment);

		mailSender.send(message);
	}

	private JavaMailSenderImpl buildSender(EmailConfiguration config) {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(config.getSmtpHost());
		sender.setPort(config.getSmtpPort());
		sender.setUsername(config.getUsername());
		sender.setPassword(config.getPassword());
		sender.setDefaultEncoding(StandardCharsets.UTF_8.name());

		Properties props = sender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", String.valueOf(config.isAuthEnabled()));
		props.put("mail.smtp.starttls.enable", String.valueOf(config.isStartTlsEnabled()));

		return sender;
	}

	private void setFrom(MimeMessage message, EmailConfiguration config) throws MessagingException {
		try {
			if (config.getFromName() != null && !config.getFromName().isBlank()) {
				message.setFrom(new InternetAddress(config.getFromAddress(), config.getFromName()));
			} else {
				message.setFrom(config.getFromAddress());
			}
		} catch (Exception ex) {
			throw new MessagingException("Invalid from address/name.", ex);
		}
	}

}

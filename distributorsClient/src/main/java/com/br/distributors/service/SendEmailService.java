package com.br.distributors.service;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;

@Service
public class SendEmailService {

	private final JavaMailSender mailSender;
	private final Environment env;

	public SendEmailService(JavaMailSender mailSender, Environment env) {
		this.mailSender = mailSender;
		this.env = env;
	}

	public SendResult send(String to, String subject, String content, boolean isHtml) {
		try {
			validateEmail(to);

			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, "UTF-8");
			helper.setValidateAddresses(true);

			message.setFrom(from());
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, isHtml);

			mailSender.send(message);
			return SendResult.ok(to);

		} catch (AddressException e) {
			return SendResult.permanentFail(to, "INVALID_ADDRESS", e.getMessage());
		} catch (MailSendException e) {
			return SendResult.fromMailSendException(to, e);
		} catch (MailException e) {
			return SendResult.tempFail(to, "MAIL_EXCEPTION", e.getMessage());
		} catch (MessagingException e) {
			return SendResult.tempFail(to, "MESSAGING_EXCEPTION", e.getMessage());
		}
	}

	public SendResult sendWithAttachment(String to, String subject, String content, byte[] file, String fileName,
			boolean isHtml) {
		try {
			validateEmail(to);

			var message = mailSender.createMimeMessage();
			var helper = new MimeMessageHelper(message, true, "UTF-8"); // multipart
			helper.setValidateAddresses(true);

			message.setFrom(from());
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(content, isHtml);

			InputStreamSource attachment = new ByteArrayResource(file);
			helper.addAttachment(fileName, attachment);

			mailSender.send(message);
			return SendResult.ok(to);

		} catch (AddressException e) {
			return SendResult.permanentFail(to, "INVALID_ADDRESS", e.getMessage());
		} catch (MailSendException e) {
			return SendResult.fromMailSendException(to, e);
		} catch (MailException e) {
			return SendResult.tempFail(to, "MAIL_EXCEPTION", e.getMessage());
		} catch (MessagingException e) {
			return SendResult.tempFail(to, "MESSAGING_EXCEPTION", e.getMessage());
		}
	}

	private String from() {
		return env.getProperty("spring.mail.username");
	}

	private void validateEmail(String email) throws AddressException {
		var addr = new InternetAddress(email);
		addr.validate();
	}

	public static class SendResult {
		public final String to;
		public final boolean success;
		public final boolean permanent;
		public final String code;
		public final String detail;

		private SendResult(String to, boolean success, boolean permanent, String code, String detail) {
			this.to = to;
			this.success = success;
			this.permanent = permanent;
			this.code = code;
			this.detail = detail;
		}

		public static SendResult ok(String to) {
			return new SendResult(to, true, false, "OK", null);
		}

		public static SendResult permanentFail(String to, String code, String detail) {
			return new SendResult(to, false, true, code, detail);
		}

		public static SendResult tempFail(String to, String code, String detail) {
			return new SendResult(to, false, false, code, detail);
		}

		public static SendResult fromMailSendException(String to, MailSendException e) {
			String msg = String.valueOf(e.getMessage());

			if (msg.contains(" 5.1.1") || msg.contains("User unknown") || msg.contains("Invalid Addresses")) {
				return permanentFail(to, "SMTP_511_INVALID", msg);
			}
			if (msg.contains("5.2.2") || msg.contains("Mailbox full")) {
				return tempFail(to, "SMTP_522_MAILBOX_FULL", msg);
			}
			if (msg.contains("5.7.1") && (msg.toLowerCase().contains("limit") || msg.toLowerCase().contains("rate"))) {
				return tempFail(to, "RATE_LIMIT", msg);
			}
			return tempFail(to, "MAIL_SEND_EXCEPTION", msg);
		}
	}
}
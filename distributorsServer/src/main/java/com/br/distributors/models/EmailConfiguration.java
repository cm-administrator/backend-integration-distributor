package com.br.distributors.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tbEmailConfiguration")
@Schema(name = "EmailConfiguration", description = "SMTP configuration per distributor.")
public class EmailConfiguration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Schema(description = "Primary key.")
	private Long id;

	@Column(nullable = false)
	@Schema(description = "SMTP server host.", example = "smtp.gmail.com")
	private String smtpHost;

	@Column(nullable = false)
	@Schema(description = "SMTP server port.", example = "587")
	private Integer smtpPort;

	@Column(nullable = false)
	@Schema(description = "SMTP username/login.", example = "no-reply@empresa.com.br")
	private String username;

	@Column(nullable = false)
	@Schema(description = "Encrypted SMTP password (never store plaintext).")
	private String password;

	@Column(nullable = false)
	@Schema(description = "Email address used as From.", example = "no-reply@empresa.com.br")
	private String fromAddress;

	@Schema(description = "Optional From name.", example = "Distribuidor X")
	private String fromName;

	@Column(nullable = false)
	@Schema(description = "Enable STARTTLS.", example = "true")
	private boolean startTlsEnabled;

	@Column(nullable = false)
	@Schema(description = "Enable SMTP AUTH.", example = "true")
	private boolean authEnabled;

	@Column(nullable = false)
	@Schema(description = "Configuration active flag.", example = "true")
	private boolean active;

	protected EmailConfiguration() {
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}

	public Integer getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(Integer smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public boolean isStartTlsEnabled() {
		return startTlsEnabled;
	}

	public void setStartTlsEnabled(boolean startTlsEnabled) {
		this.startTlsEnabled = startTlsEnabled;
	}

	public boolean isAuthEnabled() {
		return authEnabled;
	}

	public void setAuthEnabled(boolean authEnabled) {
		this.authEnabled = authEnabled;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getId() {
		return id;
	}

}

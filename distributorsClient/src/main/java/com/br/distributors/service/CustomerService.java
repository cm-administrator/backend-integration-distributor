package com.br.distributors.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.br.distributors.models.CustomerFile;
import com.br.distributors.models.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomerService {

	// Último campo do detalhe:
	// Representatividade: tamanho 6, posição inicial 321 => termina na posição 326.
	private static final int MIN_LEN_DETALHE = 326;
	private static final int MIN_LEN_HEADER = 22; // H(1) + PDV10(7) + CNPJ(14) => 1..22

	public String converter(Path path) throws IOException {

		CustomerFile arquivo = parse(path);

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(arquivo);
		return json;
	}

	public static CustomerFile parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		CustomerFile out = new CustomerFile();
		out.customers = new ArrayList<>();

		for (String raw : lines) {
			if (raw == null || raw.isEmpty())
				continue;

			// NÃO usar stripTrailing/trim: o arquivo é posicional.
			String line = raw;

			char tipo = line.charAt(0);
			switch (tipo) {
			case 'H' -> parseHeader(line, out);
			case 'D' -> {
				Customer c = parseDetalhe(line);
				if (c != null)
					out.customers.add(c);
			}
			default -> {
				// ignora
			}
			}
		}

		return out;
	}

	/**
	 * Header (manual): Tipo: pos 1 tam 1 => "H" Identificador: pos 2 tam 7 =>
	 * "PDV10" CNPJ Fornecedor: pos 9 tam 14 :contentReference[oaicite:2]{index=2}
	 */
	private static void parseHeader(String line, CustomerFile out) {
		String padded = padRight(line, MIN_LEN_HEADER);

		out.headerType = "H";
		out.identifier = slice(padded, 2, 7).trim(); // PDV10
		out.supplierIdentifier = onlyDigits(slice(padded, 9, 14));
	}

	/**
	 * Detalhe (manual - clientes PDV10): Tipo: pos 1 tam 1 => "D" CNPJ Agente
	 * Distribuição: pos 2 tam 14 Identificação do cliente: pos 16 tam 18 Razão
	 * social: pos 34 tam 40 Endereço: pos 74 tam 40 Bairro: pos 114 tam 30 CEP: pos
	 * 144 tam 9 Cidade: pos 153 tam 30 Estado: pos 183 tam 30 (conteúdo esperado =
	 * UF com 2 chars) Nome Responsável: pos 213 tam 20 Telefones: pos 233 tam 40
	 * CNPJ/CPF cliente: pos 273 tam 18 Rota: pos 291 tam 10 Campo reservado: pos
	 * 301 tam 10 Tipo de Loja: pos 311 tam 10 Representatividade: pos 321 tam 6
	 * (9(3).9(2)) :contentReference[oaicite:3]{index=3}
	 */
	private static Customer parseDetalhe(String line) {
		String padded = padRight(line, MIN_LEN_DETALHE);
		if (padded.charAt(0) != 'D')
			return null;

		Customer c = new Customer();

		c.distributorAgentIdentifier = onlyDigits(slice(padded, 2, 14));
		c.identifier = slice(padded, 16, 18).trim();

		c.legalName = slice(padded, 34, 40).trim();
		c.address = slice(padded, 74, 40).trim();
		c.neighborhood = slice(padded, 114, 30).trim();

		c.zipCode = slice(padded, 144, 9).trim(); // ex: 78780-000 (pode vir colado com cidade no texto, mas aqui é
													// fixo)

		c.city = slice(padded, 153, 30).trim();

		// manual dá 30, mas conteúdo esperado é UF (2 chars); por segurança, pega trim
		// e se tiver > 2, usa só 2.
		String ufRaw = slice(padded, 183, 30).trim();
		c.state = ufRaw.length() >= 2 ? ufRaw.substring(0, 2) : ufRaw;

		c.responsibleName = slice(padded, 213, 20).trim();
		c.phoneNumbers = slice(padded, 233, 40).trim();

		// c.customerIdentifier2 = slice(padded, 273, 18).trim();
		c.route = slice(padded, 291, 10).trim();
		c.storeType = slice(padded, 311, 10).trim();

		String repStr = slice(padded, 321, 6).trim(); // formato 999.99
		c.representativity = parseBigDecimalNullable(repStr);

		return c;
	}

	// ===== helpers (posições 1-based) =====

	private static String slice(String s, int start1Based, int length) {
		int start = Math.max(0, start1Based - 1);
		int end = Math.min(s.length(), start + length);
		if (start >= s.length())
			return "";
		return s.substring(start, end);
	}

	private static String padRight(String s, int minLen) {
		if (s.length() >= minLen)
			return s;
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < minLen)
			sb.append(' ');
		return sb.toString();
	}

	private static String onlyDigits(String s) {
		if (s == null)
			return null;
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (Character.isDigit(ch))
				out.append(ch);
		}
		return out.toString();
	}

	private static BigDecimal parseBigDecimalNullable(String s) {
		if (s == null)
			return null;
		String t = s.trim();
		if (t.isEmpty())
			return null;
		try {
			return new BigDecimal(t);
		} catch (Exception e) {
			return null;
		}
	}
}

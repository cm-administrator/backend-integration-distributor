package com.br.distributors.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.br.distributors.request.SalesPerson;
import com.br.distributors.request.SalesPersonFile;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SalesPersonService {

	// Último campo do detalhe: Nome do Vendedor, tamanho 50, posição inicial 180 =>
	// termina em 229
	private static final int MIN_LEN_DETALHE = 229;

	// Header: 1 + 7 + 14 = 22
	private static final int MIN_LEN_HEADER = 22;

	public String converter(Path path) throws IOException {

		SalesPersonFile arquivo = parse(path);

		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(arquivo);
		return json;
	}

	public static SalesPersonFile parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		SalesPersonFile out = new SalesPersonFile();
		out.details = new ArrayList<>();

		for (String raw : lines) {
			if (raw == null || raw.isEmpty())
				continue;

			// NÃO usar trim/stripTrailing na linha inteira (layout posicional).
			String line = raw;

			char tipo = line.charAt(0);
			switch (tipo) {
			case 'H' -> parseHeader(line, out);
			case 'D' -> {
				SalesPerson d = parseDetalhe(line);
				if (d != null)
					out.details.add(d);
			}
			default -> {
				// ignora
			}
			}
		}

		return out;
	}

	/**
	 * Header FV10 (manual): Tipo do Registro: pos 1 tam 1 = "H" Identificador: pos
	 * 2 tam 7 = "FV10" CNPJ Fornecedor: pos 9 tam 14
	 * :contentReference[oaicite:2]{index=2}
	 */
	private static void parseHeader(String line, SalesPersonFile out) {
		String padded = padRight(line, MIN_LEN_HEADER);

		out.headerType = "H";
		out.identifier = slice(padded, 2, 7).trim(); // FV10
		out.supplierIdentifier = onlyDigits(slice(padded, 9, 14));
	}

	/**
	 * Detalhe FV10 (manual): Tipo: pos 1 tam 1 = "D" CNPJ Agente Distribuição: pos
	 * 2 tam 14 Identificação cliente: pos 16 tam 18 Código do Gerente: pos 34 tam
	 * 13 Nome do Gerente: pos 47 tam 50 Código do Supervisor: pos 97 tam 13 Nome do
	 * Supervisor: pos 110 tam 50 Código do Vendedor: pos 160 tam 20 Nome do
	 * Vendedor: pos 180 tam 50 :contentReference[oaicite:3]{index=3}
	 */
	private static SalesPerson parseDetalhe(String line) {
		String padded = padRight(line, MIN_LEN_DETALHE);
		if (padded.isEmpty() || padded.charAt(0) != 'D')
			return null;

		SalesPerson d = new SalesPerson();

		d.distributorAgentIdentifier = onlyDigits(slice(padded, 2, 14));
		d.customerIdentifier = slice(padded, 16, 18).trim();

		d.salesManagerCode = slice(padded, 34, 13).trim();
		d.salesManagerName = slice(padded, 47, 50).trim();

		d.salesSupervisorCode = slice(padded, 97, 13).trim();
		d.salesSupervisorName = slice(padded, 110, 50).trim();

		d.salespersonCode = slice(padded, 160, 20).trim();
		d.salespersonName = slice(padded, 180, 50).trim();

		return d;
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
}

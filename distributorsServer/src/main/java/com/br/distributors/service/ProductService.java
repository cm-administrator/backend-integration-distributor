package com.br.distributors.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.br.distributors.request.Product;
import com.br.distributors.request.ProductFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class ProductService {

	// Pelo layout: último campo termina em 277 (251 + 27 - 1)
	private static final int MIN_LEN_HEADER = 19; // 1 + 10 + 8
	private static final int MIN_LEN_IDENT = 15; // 1 + 14
	private static final int MIN_LEN_PROD = 277;

	private static final DateTimeFormatter BASIC = DateTimeFormatter.BASIC_ISO_DATE;

	public String converter(Path path) throws IOException {

		ProductFile arquivo = parse(path);
		ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		String json = mapper.writeValueAsString(arquivo);
		return json;
	}

	public static ProductFile parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		ProductFile out = new ProductFile();
		out.identifiers = new ArrayList<>();
		out.products = new ArrayList<>();

		for (String raw : lines) {
			if (raw == null || raw.isEmpty())
				continue;

			// NÃO usar stripTrailing aqui: o layout é por posição fixa.
			String line = raw;

			char tipo = line.charAt(0);

			switch (tipo) {
			case 'H' -> parseHeader(line, out);
			case 'I' -> parseIdentificador(line, out);
			case 'V' -> {
				Product p = parseProduct(line);
				if (p != null)
					out.products.add(p);
			}
			default -> {
				// ignora linhas inesperadas
			}
			}
		}
		return out;
	}

	/**
	 * Header (Tipo = H) pos 1: Tipo (1) => "H" pos 2: Identificador (10) =>
	 * "CADPROD" pos 12: Data (8) => AAAAMMDD
	 */
	private static void parseHeader(String line, ProductFile out) {
		String padded = padRight(line, MIN_LEN_HEADER);

		out.headerType = "H";
		out.layout = slice(padded, 2, 10).trim(); // "CADPROD"
		String yyyymmdd = slice(padded, 12, 8).trim();

		if (!yyyymmdd.isEmpty()) {
			out.generationDate = LocalDate.parse(yyyymmdd, BASIC);
		}
	}

	/**
	 * Identificador (Tipo = I) pos 1: Tipo (1) => "I" pos 2: CNPJ Agente
	 * Distribuição (14)
	 */
	private static void parseIdentificador(String line, ProductFile out) {
		String padded = padRight(line, MIN_LEN_IDENT);
		String cnpjAgente = slice(padded, 2, 14).trim();
		if (!cnpjAgente.isEmpty())
			out.identifiers.add(cnpjAgente);
	}

	/**
	 * Product (Tipo = V) - por posições fixas (manual) pos 1: Tipo (1) => "V" pos
	 * 2: CNPJ do fornecedor (18) (CNPJ sem máscara dentro do campo) pos 20: Razão
	 * social fornecedor (30) pos 50: Código do Product (14) (código interno no
	 * agente) pos 64: Tipo de Embalagem (1) ("0" ou "1") pos 65: Código de barras
	 * (14) (EAN13 ou DUN14) pos 79: Tipo do código de barras (1) ("1","2" ou "3")
	 * pos 80: Nome do Product (100) pos 180: Divisão do Product (40) pos 220: Campo
	 * reservado (30) -> ignorar pos 250: Status do Product (1) ("A" ou "I") pos
	 * 251: Campo reservado (27) -> ignorar
	 */
	private static Product parseProduct(String line) {
		String padded = padRight(line, MIN_LEN_PROD);

		// valida tipo
		if (padded.isEmpty() || padded.charAt(0) != 'V')
			return null;

		Product p = new Product();
		p.supplierIdentifier = slice(padded, 2, 18).trim();
		p.supplierLegalName = slice(padded, 20, 30).trim();
		p.code = slice(padded, 50, 14).trim();
		p.packagingType = slice(padded, 64, 1).trim();
		p.barcode = slice(padded, 65, 14).trim();
		p.barcodeType = slice(padded, 79, 1).trim();
		p.name = slice(padded, 80, 100).trim();
		p.division = slice(padded, 180, 40).trim();
		p.status = slice(padded, 250, 1).trim();

		return p;
	}

	// ===== helpers (posições 1-based) =====

	private static String slice(String line, int start1Based, int length) {
		int start = Math.max(0, start1Based - 1);
		int end = Math.min(line.length(), start + length);
		if (start >= line.length())
			return "";
		return line.substring(start, end);
	}

	private static String padRight(String s, int minLen) {
		if (s.length() >= minLen)
			return s;
		StringBuilder sb = new StringBuilder(s);
		while (sb.length() < minLen)
			sb.append(' ');
		return sb.toString();
	}
}

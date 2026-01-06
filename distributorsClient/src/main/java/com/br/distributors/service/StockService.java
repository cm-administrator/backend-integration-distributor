package com.br.distributors.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.br.distributors.models.StockFile;
import com.br.distributors.models.Stock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class StockService {

	private static final DateTimeFormatter BASIC = DateTimeFormatter.BASIC_ISO_DATE;

	// Header real:
	// HESTOQ111475769500024620251231
	// H + IDENT + CNPJ(14) + DATA(8)
	private static final Pattern HEADER = Pattern.compile("^H([A-Z0-9]+?)(\\d{14})(\\d{8})\\s*$");

	// Detalhe real (tokens):
	// E + CNPJ(14) + CODIGO_VARIAVEL + QTD_DECIMAL_4 + ... + ULTIMO_TOKEN
	// Ex:
	// E169292820001467896629630086 000000000000428.0000 000000000001
	private static final Pattern DETALHE = Pattern.compile("^E(\\d{14})(\\S+)\\s+(\\d+\\.\\d{4})(.*)$");

	public String converter(Path path) throws IOException {

		StockFile arquivo = parse(path);

		ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		String json = mapper.writeValueAsString(arquivo);
		return json;
	}

	public static StockFile parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		StockFile out = new StockFile();
		out.items = new ArrayList<>();

		for (String raw : lines) {
			String line = raw == null ? "" : raw.stripTrailing();
			if (line.isBlank())
				continue;

			char tipo = line.charAt(0);

			if (tipo == 'H') {
				parseHeader(line, out);
			} else if (tipo == 'E') {
				Stock it = parseDetalhe(line);
				if (it != null)
					out.items.add(it);
			}
		}

		return out;
	}

	private static void parseHeader(String line, StockFile out) {
		Matcher m = HEADER.matcher(line.trim());
		if (!m.matches())
			return;

		out.identifier = m.group(1).trim();
		out.supplierIdentifier = m.group(2).trim();
		out.stockDate = LocalDate.parse(m.group(3).trim(), BASIC);
	}

	private static Stock parseDetalhe(String line) {
		Matcher m = DETALHE.matcher(line.trim());
		if (!m.matches())
			return null;

		String cnpj = m.group(1);
		String codigoProduto = m.group(2);
		String qtdStr = m.group(3);
		String resto = m.group(4) == null ? "" : m.group(4).trim();

		Stock it = new Stock();
		it.distributorAgentIdentifier = cnpj;
		it.productCode = codigoProduto;

		// QUANTIDADE: sempre vem no token 3 (ex.: 000000000000428.0000)
		it.quantity = new BigDecimal(qtdStr);

		// O que sobra depois da quantidade pode ter vários brancos.
		// Regra segura:
		// - se houver tokens no "resto", pega o ÚLTIMO como codigoLote
		// - se algum token tiver 8 dígitos e for data válida, vira validadeLote
		if (!resto.isBlank()) {
			String[] tokens = resto.split("\\s+");

			// validade AAAAMMDD (se existir)
			for (String t : tokens) {
				if (looksLikeDate(t)) {
					it.batchExpirationDate = LocalDate.parse(t, BASIC);
					break;
				}
			}

			// lote = último token (no seu exemplo: 000000000001)
			String last = tokens[tokens.length - 1].trim();
			it.batchCode = last.isBlank() ? null : last;
		}

		return it;
	}

	private static boolean looksLikeDate(String s) {
		if (s == null || s.length() != 8)
			return false;
		for (int i = 0; i < 8; i++) {
			if (!Character.isDigit(s.charAt(i)))
				return false;
		}
		try {
			LocalDate.parse(s, BASIC);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

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

import com.br.distributors.request.Sales;
import com.br.distributors.request.SalesFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class SalesService {

	private static final DateTimeFormatter BASIC = DateTimeFormatter.BASIC_ISO_DATE;

	// Header real:
	// HVENDA1114757695000246
	private static final Pattern HEADER = Pattern.compile("^H([A-Za-z0-9]+?)(\\d{14})\\s*$");

	// Detalhe real (conforme seu arquivo):
	// D + CNPJ(14) + CLIENTE(14) + espaços +
	// DATASEQ(14) + espaços + EAN + espaços +
	// QTD (....\.\d{4}) + PRECO+VENDEDOR (....\.\d{6}) COLADOS + espaços +
	// DOC + espaços + CAMPO7 + espaços + CAMPO8
	//
	// Exemplo:
	// D1692928200014611285151000150 20251206637905 7896629630277
	// 000000000000008.000000020.514700 N78785-000 00000000 00000.000001
	//
	// Observação importante:
	// - QTD = 4 casas decimais
	// - PRECO+VENDEDOR = 2 casas decimais + 4 dígitos do vendedor (total 6)
	private static final Pattern DETALHE = Pattern.compile("^D" + "(\\d{14})" + // 1 cnpj distribuidor
			"(\\d{14})" + // 2 id cliente
			"\\s+" + "(\\d{14})" + // 3 dataseq (AAAAMMDD + 6)
			"\\s+" + "([0-9]{1,18})" + // 4 ean/codigo (tamanho variável)
			"\\s+" + "(\\d+\\.\\d{4})" + // 5 quantidade (4 casas)
			"(\\d+\\.\\d{6})" + // 6 preco+vendedor (2 casas + 4 dígitos) COLADO
			"\\s+" + "([A-Z][0-9A-Z\\-]{3,20})" + // 7 documento (N..., B..., etc.)
			"\\s+" + "(\\S+)" + // 8 campo7
			"\\s+" + "(\\S+)" + // 9 campo8
			"\\s*$");

	public String converter(Path path) throws IOException {

		SalesFile arquivo = parse(path);

		ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		String json = mapper.writeValueAsString(arquivo);
		return json;
	}

	public static SalesFile parse(Path path) throws IOException {
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

		SalesFile out = new SalesFile();
		out.items = new ArrayList<>();

		for (String raw : lines) {
			String line = raw == null ? "" : raw.stripTrailing();
			if (line.isBlank())
				continue;

			char tipo = line.charAt(0);

			switch (tipo) {
			case 'H' -> parseHeader(line, out);
			case 'D' -> {
				Sales item = parseDetalhe(line);
				if (item != null)
					out.items.add(item);
			}
			default -> {
				// ignora
			}
			}
		}

		return out;
	}

	private static void parseHeader(String line, SalesFile out) {
		Matcher m = HEADER.matcher(line.trim());
		if (!m.matches())
			return;

		out.layout = m.group(1).trim(); // VENDA1
		out.supplierIdentifier = m.group(2).trim(); // 14757695000246
	}

	private static Sales parseDetalhe(String line) {
		Matcher m = DETALHE.matcher(line);
		if (!m.matches()) {
			// Se alguma linha vier truncada/incompleta, não quebra o processo
			return null;
		}

		Sales it = new Sales();
		it.distributorIdentifier = m.group(1);
		it.customerId = m.group(2);

		String dataSeq = m.group(3);
		it.transactionDate = LocalDate.parse(dataSeq.substring(0, 8), BASIC);
		it.sequence = dataSeq.substring(8); // 6

		it.productEan = m.group(4);

		it.quantity = new BigDecimal(m.group(5)); // ex.: 000...008.0000

		// preco+vendedor: ex.: 000000020.514700
		// - vendedor = últimos 4 dígitos (4700)
		// - preço = restante, com 2 casas decimais (000000020.51)
		String precoVendedor = m.group(6);
		if (precoVendedor.length() < 5)
			return null;

		it.salespersonCode = precoVendedor.substring(precoVendedor.length() - 4);
		String precoStr = precoVendedor.substring(0, precoVendedor.length() - 4);

		// Se por algum motivo vier sem ponto no trecho do preço, não tenta "inventar".
		// Aqui o seu padrão tem ponto (20.51).
		it.salePrice = new BigDecimal(precoStr);

		it.document = m.group(7);
		it.field7 = m.group(8);
		it.field8 = m.group(9);

		return it;
	}
}

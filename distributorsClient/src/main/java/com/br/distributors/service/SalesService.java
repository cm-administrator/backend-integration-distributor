package com.br.distributors.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.br.distributors.models.Sales;
import com.br.distributors.models.SalesFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class SalesService {

	private static final DateTimeFormatter BASIC = DateTimeFormatter.BASIC_ISO_DATE;

	private static final Pattern HEADER = Pattern.compile("^H([A-Za-z0-9]+?)(\\d{14})\\s*$");

	private static final Pattern DETALHE = Pattern.compile("^D" + "(\\d{14})" + // 1 cnpj distribuidor
			"(\\d{14})" + // 2 id cliente
			"\\s+" + "(\\d{14})" + // 3 dataseq (AAAAMMDD + 6)
			"\\s+" + "([0-9]{1,18})" + // 4 ean/codigo
			"\\s+" + "(\\d+\\.\\d{4})" + // 5 quantidade
			"(\\d+\\.\\d{6})" + // 6 preco+vendedor (2 casas + 4 vendedor) colados
			"\\s+" + "([A-Z][0-9A-Z\\-]{3,20})" + // 7 documento
			"\\s+" + "(\\S+)" + // 8 campo7
			"\\s+" + "(\\S+)" + // 9 campo8
			"\\s*$");

	private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public String converter(Path path) throws IOException {
		SalesFile arquivo = parse(path);
		return mapper.writeValueAsString(arquivo);
	}

	public static SalesFile parse(Path path) throws IOException {
		SalesFile out = new SalesFile();
		out.items = new ArrayList<>();

		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String raw;
			while ((raw = br.readLine()) != null) {
				String line = raw == null ? "" : raw.stripTrailing();
				if (line.isBlank())
					continue;

				char tipo = line.charAt(0);
				if (tipo == 'H') {
					parseHeader(line, out);
				} else if (tipo == 'D') {
					Sales item = parseDetalhe(line);
					if (item != null)
						out.items.add(item);
				}
			}
		}
		return out;
	}

	public boolean postarEmLotes(Path path, int batchSize, IntegrationApiClient client) throws IOException {
		if (batchSize <= 0)
			throw new IllegalArgumentException("batchSize deve ser > 0");

		SalesFile headerBase = new SalesFile();
		boolean headerLido = false;

		int totalEnviados = 0;
		int lote = 0;

		SalesFile batch = newBatch(headerBase, batchSize);

		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String raw;
			while ((raw = br.readLine()) != null) {
				String line = raw == null ? "" : raw.stripTrailing();
				if (line.isBlank())
					continue;

				char tipo = line.charAt(0);
				if (tipo == 'H') {
					parseHeader(line, headerBase);
					headerLido = true;
					copyHeaderToBatch(headerBase, batch);
					continue;
				}

				if (tipo != 'D')
					continue;

				// opcional: se quiser abortar quando vier detalhe antes do header
				// if (!headerLido) throw new IllegalStateException("Detalhe antes do header");

				Sales item = parseDetalhe(line);
				if (item == null)
					continue;

				batch.items.add(item);

				if (batch.items.size() >= batchSize) {
					lote++;
					int offset = totalEnviados;
					int qtd = batch.items.size();

					if (!enviarBatch(client, batch, lote, offset))
						return false;

					totalEnviados += qtd;
					batch = newBatch(headerBase, batchSize);
					if (headerLido)
						copyHeaderToBatch(headerBase, batch);
				}
			}
		}

		// restante
		if (batch.items != null && !batch.items.isEmpty()) {
			lote++;
			if (!enviarBatch(client, batch, lote, totalEnviados))
				return false;
		}

		return true;
	}

	private SalesFile newBatch(SalesFile headerBase, int batchSize) {
		SalesFile b = new SalesFile();
		b.items = new ArrayList<>(batchSize);
		// header será copiado quando disponível
		b.layout = headerBase.layout;
		b.supplierIdentifier = headerBase.supplierIdentifier;
		return b;
	}

	private void copyHeaderToBatch(SalesFile headerBase, SalesFile batch) {
		batch.layout = headerBase.layout;
		batch.supplierIdentifier = headerBase.supplierIdentifier;
	}

	private boolean enviarBatch(IntegrationApiClient client, SalesFile batch, int lote, int offset) throws IOException {
		final String json = mapper.writeValueAsString(batch);

		final int maxTentativas = 3;
		long esperaMs = 800; // menor e com backoff

		int qtd = batch.items.size();

		for (int tentativa = 1; tentativa <= maxTentativas; tentativa++) {
			var resp = client.postSales(json);

			boolean httpOk = resp.status() >= 200 && resp.status() < 300;

			// REGRA DE SUCESSO (escolha UMA):
			// 1) Se seu contrato é "2xx = enviado com sucesso":
			boolean ok = httpOk;

			// 2) Se seu contrato é "success=true é o que vale":
			// boolean ok = resp.success();

			if (ok) {
				// Log só do sucesso (evita “falou erro mas enviou”)
				System.out.println("POST /vendas lote " + lote + " OK (itens " + offset + " a " + (offset + qtd - 1)
						+ ")" + (tentativa > 1 ? " após " + tentativa + " tentativas" : ""));
				return true;
			}

			if (tentativa < maxTentativas) {
				// Log curto de retry (sem “erro” definitivo)
				System.out.println("POST /vendas lote " + lote + " retry " + tentativa + "/" + maxTentativas
						+ " status=" + resp.status() + " success=" + resp.success());

				sleepQuiet(esperaMs);
				esperaMs = Math.min(esperaMs * 2, 5000);
			} else {
				// Log do erro final
				System.out.println("POST /vendas lote " + lote + " FALHOU" + " (itens " + offset + " a "
						+ (offset + qtd - 1) + ")" + " status=" + resp.status() + " success=" + resp.success());
			}
		}

		return false;
	}

	private void sleepQuiet(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private static void parseHeader(String line, SalesFile out) {
		Matcher m = HEADER.matcher(line.trim());
		if (!m.matches())
			return;
		out.layout = m.group(1).trim();
		out.supplierIdentifier = m.group(2).trim();
	}

	private static Sales parseDetalhe(String line) {
		Matcher m = DETALHE.matcher(line);
		if (!m.matches())
			return null;

		Sales it = new Sales();
		it.distributorIdentifier = m.group(1);
		it.customerIdentifier = m.group(2);

		String dataSeq = m.group(3);
		it.transactionDate = LocalDate.parse(dataSeq.substring(0, 8), BASIC);
		it.sequence = dataSeq.substring(8);

		it.productBarcode = m.group(4);
		it.quantity = new BigDecimal(m.group(5));

		String precoVendedor = m.group(6);
		if (precoVendedor.length() < 5)
			return null;

		it.salespersonCode = precoVendedor.substring(precoVendedor.length() - 4);
		String precoStr = precoVendedor.substring(0, precoVendedor.length() - 4);
		it.salePrice = new BigDecimal(precoStr);

		it.document = m.group(7);
		it.field7 = m.group(8);
		it.field8 = m.group(9);

		return it;
	}
}

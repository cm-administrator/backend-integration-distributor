package com.br.distributors.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.br.distributors.service.RecentlyReadFilesApiClient.RecentlyReadFilesDto;

@Service
public class IntegrationService {

	private static final String PREFIX_CLIENTES = "CLIENTESCAN";
	private static final String PREFIX_ESTOQUE = "ESTOQUECAN";
	private static final String PREFIX_PRODUTOS = "PRODUTOSCAN";
	private static final String PREFIX_VENDAS = "VENDASCAN";
	private static final String PREFIX_FORCA_VENDAS = "FORCAVENDASCAN";
	private static final int SALES_BATCH_SIZE = 500;

	// timestamp do nome: ddMMyyyyHHmmssSSS (ex: 05012026135328215)
	private static final DateTimeFormatter FILE_TS_FMT = DateTimeFormatter.ofPattern("ddMMyyyyHHmmssSSS", Locale.ROOT);

	private final AtomicBoolean running = new AtomicBoolean(false);

	private final CustomerService clienteService;
	private final StockService estoqueService;
	private final ProductService produtoService;
	private final SalesService vendasService;
	private final SalesPersonService salesPersonService;
	private final IntegrationApiClient integrationApiClient;
	private final RecentlyReadFilesApiClient recentlyReadFilesApiClient;

	/**
	 * No application.properties, exemplo:
	 * integracao.pasta=C:\\Users\\IuriSouza\\Downloads\\canto-de-minas
	 */
	@Value("${integracao.pasta}")
	private String pastaIntegracao;

	/**
	 * Identifier do distribuidor desta aplicação (para buscar/salvar o último
	 * arquivo lido no outro sistema)
	 */
	@Value("${distributor.identifier}")
	private String distributorIdentifier;

	public IntegrationService(CustomerService clienteService, StockService estoqueService,
			ProductService produtoService, SalesService vendasService, SalesPersonService salesPersonService,
			IntegrationApiClient integrationApiClient, RecentlyReadFilesApiClient recentlyReadFilesApiClient) {
		this.clienteService = clienteService;
		this.estoqueService = estoqueService;
		this.produtoService = produtoService;
		this.vendasService = vendasService;
		this.salesPersonService = salesPersonService;
		this.integrationApiClient = integrationApiClient;
		this.recentlyReadFilesApiClient = recentlyReadFilesApiClient;
	}

	@Scheduled(fixedRate = 5000)
	public void integrar() throws IOException {

		if (!running.compareAndSet(false, true)) {
			System.err.println("Integração já em execução, ignorando disparo.");
			return;
		}

		try {
			Path dir = Path.of(pastaIntegracao);

			Path arqClientes = findMaisRecentePorTimestampNoNome(dir, PREFIX_CLIENTES).orElseThrow(
					() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_CLIENTES + " em " + dir));

			Path arqProdutos = findMaisRecentePorTimestampNoNome(dir, PREFIX_PRODUTOS).orElseThrow(
					() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_PRODUTOS + " em " + dir));

			Path arqForcaVendas = findMaisRecentePorTimestampNoNome(dir, PREFIX_FORCA_VENDAS).orElseThrow(
					() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_FORCA_VENDAS + " em " + dir));

			Path arqVendas = findMaisRecentePorTimestampNoNome(dir, PREFIX_VENDAS).orElseThrow(
					() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_VENDAS + " em " + dir));

			Path arqEstoque = findMaisRecentePorTimestampNoNome(dir, PREFIX_ESTOQUE).orElseThrow(
					() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_ESTOQUE + " em " + dir));

			// Busca último lido no sistema remoto (404 => null)
			RecentlyReadFilesDto last = recentlyReadFilesApiClient.getByDistributorIdentifier(distributorIdentifier);

			Instant tsClientes = fileInstantFromNameOrLastModified(arqClientes, PREFIX_CLIENTES);
			Instant tsProdutos = fileInstantFromNameOrLastModified(arqProdutos, PREFIX_PRODUTOS);
			Instant tsForca = fileInstantFromNameOrLastModified(arqForcaVendas, PREFIX_FORCA_VENDAS);
			Instant tsVendas = fileInstantFromNameOrLastModified(arqVendas, PREFIX_VENDAS);
			Instant tsEstoque = fileInstantFromNameOrLastModified(arqEstoque, PREFIX_ESTOQUE);

			boolean newClientes = isNewer(tsClientes, last == null ? null : last.getCustomersInstant());
			boolean newProdutos = isNewer(tsProdutos, last == null ? null : last.getProductInstant());
			boolean newForca = isNewer(tsForca, last == null ? null : last.getSalesPersonInstant());
			boolean newVendas = isNewer(tsVendas, last == null ? null : last.getSalesInstant());
			boolean newEstoque = isNewer(tsEstoque, last == null ? null : last.getStockInstant());

			// Não tem nada novo => sai
			if (!(newClientes || newProdutos || newForca || newVendas || newEstoque)) {
				return;
			}

			// Converte (mantive seu fluxo enviando todos; se quiser eu ajusto pra enviar só
			// os "new*")
			String clientesJson = clienteService.converter(arqClientes);
			String produtosJson = produtoService.converter(arqProdutos);
			String forcaVendasJson = salesPersonService.converter(arqForcaVendas);
			String estoqueJson = estoqueService.converter(arqEstoque);

			// POSTs na ordem: customer -> products -> salesperson -> sales -> stock
			var rClientes = integrationApiClient.postCustomers(clientesJson);
			if (!rClientes.success()) {
				System.out.println("POST /clientes -> " + rClientes.status() + " success=" + rClientes.success());
				return;
			}

			var rProdutos = integrationApiClient.postProducts(produtosJson);
			if (!rProdutos.success()) {
				System.out.println("POST /produtos -> " + rProdutos.status() + " success=" + rProdutos.success());
				return;
			}

			var rForca = integrationApiClient.postSalesPersons(forcaVendasJson);
			if (!rForca.success()) {
				System.out.println("POST /vendedores -> " + rForca.status() + " success=" + rForca.success());
				return;
			}

			boolean okVendas = vendasService.postarEmLotes(arqVendas, SALES_BATCH_SIZE, integrationApiClient);
			if (!okVendas) {
				System.out.println("POST /vendas -> falhou em algum lote");
				return;
			}

			var rEstoque = integrationApiClient.postStock(estoqueJson);
			if (!rEstoque.success()) {
				System.out.println("POST /estoque -> " + rEstoque.status() + " success=" + rEstoque.success());
				return;
			}

			// Atualiza checkpoint remoto SOMENTE após sucesso geral
			if (newClientes) {
				recentlyReadFilesApiClient.updateCustomersIfNewer(distributorIdentifier,
						arqClientes.getFileName().toString(), tsClientes);
			}
			if (newProdutos) {
				recentlyReadFilesApiClient.updateProductsIfNewer(distributorIdentifier,
						arqProdutos.getFileName().toString(), tsProdutos);
			}
			if (newForca) {
				recentlyReadFilesApiClient.updateSalesPersonIfNewer(distributorIdentifier,
						arqForcaVendas.getFileName().toString(), tsForca);
			}
			if (newVendas) {
				recentlyReadFilesApiClient.updateSalesIfNewer(distributorIdentifier, arqVendas.getFileName().toString(),
						tsVendas);
			}
			if (newEstoque) {
				recentlyReadFilesApiClient.updateStockIfNewer(distributorIdentifier,
						arqEstoque.getFileName().toString(), tsEstoque);
			}

			System.out.println("POST /clientes -> " + rClientes.status() + " success=" + rClientes.success());
			System.out.println("POST /produtos -> " + rProdutos.status() + " success=" + rProdutos.success());
			System.out.println("POST /vendedores -> " + rForca.status() + " success=" + rForca.success());
			System.out.println("POST /vendas -> ok=" + okVendas);
			System.out.println("POST /estoque -> " + rEstoque.status() + " success=" + rEstoque.success());

		} finally {
			running.set(false);
		}
	}

	private Optional<Path> findMaisRecentePorTimestampNoNome(Path dir, String prefixo) throws IOException {
		if (dir == null || !Files.isDirectory(dir)) {
			throw new IOException("Diretório inválido: " + dir);
		}

		String pfx = prefixo.toUpperCase(Locale.ROOT);

		try (Stream<Path> s = Files.list(dir)) {
			return s.filter(Files::isRegularFile).filter(p -> {
				String name = p.getFileName().toString().toUpperCase(Locale.ROOT);
				return name.startsWith(pfx) && name.endsWith(".TXT");
			}).map(p -> new FileCandidate(p, fileInstantFromNameOrLastModified(p, prefixo)))
					.max(Comparator.comparing(FileCandidate::instant)).map(FileCandidate::path);
		}
	}

	private Instant fileInstantFromNameOrLastModified(Path p, String prefixo) {
		return extractInstantFromFileName(p, prefixo).orElseGet(() -> safeLastModified(p).toInstant());
	}

	private Optional<Instant> extractInstantFromFileName(Path p, String prefixo) {
		String name = p.getFileName().toString().toUpperCase(Locale.ROOT);
		String pfx = prefixo.toUpperCase(Locale.ROOT);

		// remove prefixo e ".TXT"
		if (!name.startsWith(pfx) || !name.endsWith(".TXT"))
			return Optional.empty();

		String digits = name.substring(pfx.length(), name.length() - 4);
		if (!digits.matches("\\d{17}"))
			return Optional.empty();

		try {
			LocalDateTime ldt = LocalDateTime.parse(digits, FILE_TS_FMT);
			// assumir timezone do servidor; se quiser fixar: ZoneId.of("America/Sao_Paulo")
			return Optional.of(ldt.atZone(ZoneId.systemDefault()).toInstant());
		} catch (DateTimeParseException e) {
			return Optional.empty();
		}
	}

	private boolean isNewer(Instant candidate, Instant last) {
		if (candidate == null)
			return false;
		if (last == null)
			return true;
		return candidate.isAfter(last);
	}

	private FileTime safeLastModified(Path p) {
		try {
			return Files.getLastModifiedTime(p);
		} catch (IOException e) {
			return FileTime.fromMillis(0);
		}
	}

	private record FileCandidate(Path path, Instant instant) {
	}
}

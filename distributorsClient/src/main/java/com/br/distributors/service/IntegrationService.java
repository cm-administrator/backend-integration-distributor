package com.br.distributors.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class IntegrationService {

	private static final String PREFIX_CLIENTES = "CLIENTESCAN";
	private static final String PREFIX_ESTOQUE = "ESTOQUECAN";
	private static final String PREFIX_PRODUTOS = "PRODUTOSCAN";
	private static final String PREFIX_VENDAS = "VENDASCAN";
	private static final String PREFIX_FORCA_VENDAS = "FORCAVENDASCAN";

	private final CustomerService clienteService;
	private final StockService estoqueService;
	private final ProductService produtoService;
	private final SalesService vendasService;
	private final SalesPersonService salesPersonService;
	private final IntegrationApiClient integrationApiClient;

	/**
	 * No application.properties, exemplo:
	 * integracao.pasta=C:\\Users\\IuriSouza\\Downloads\\canto-de-minas
	 */
	@Value("${integracao.pasta}")
	private String pastaIntegracao;

	public IntegrationService(CustomerService clienteService, StockService estoqueService,
			ProductService produtoService, SalesService vendasService, SalesPersonService salesPersonService,
			IntegrationApiClient integrationApiClient) {
		this.clienteService = clienteService;
		this.estoqueService = estoqueService;
		this.produtoService = produtoService;
		this.vendasService = vendasService;
		this.salesPersonService = salesPersonService;
		this.integrationApiClient = integrationApiClient;
	}

	@Scheduled(fixedRate = 5000)
	public void integrar() throws IOException {
		Path dir = Path.of(pastaIntegracao);

		Path arqClientes = findMaisRecente(dir, PREFIX_CLIENTES).orElseThrow(
				() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_CLIENTES + " em " + dir));

		Path arqEstoque = findMaisRecente(dir, PREFIX_ESTOQUE)
				.orElseThrow(() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_ESTOQUE + " em " + dir));

		Path arqProdutos = findMaisRecente(dir, PREFIX_PRODUTOS).orElseThrow(
				() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_PRODUTOS + " em " + dir));

		Path arqVendas = findMaisRecente(dir, PREFIX_VENDAS)
				.orElseThrow(() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_VENDAS + " em " + dir));

		Path arqForcaVendas = findMaisRecente(dir, PREFIX_FORCA_VENDAS).orElseThrow(
				() -> new IOException("Arquivo não encontrado: prefixo " + PREFIX_FORCA_VENDAS + " em " + dir));

		// Passa o caminho COMPLETO do arquivo pra cada service
		String clientesJson = clienteService.converter(arqClientes);
		String estoqueJson = estoqueService.converter(arqEstoque);
		String produtosJson = produtoService.converter(arqProdutos);
		String vendasJson = vendasService.converter(arqVendas);
		String forcaVendasJson = salesPersonService.converter(arqForcaVendas);

		//System.err.println("Clientes: " + clientesJson);
		//System.out.println("---------------------------------------------------------------------------------------");
		//System.err.println("Estoque: " + estoqueJson);
		//System.out.println("---------------------------------------------------------------------------------------");
		//System.err.println("Produtos: " + produtosJson);
		//System.out.println("---------------------------------------------------------------------------------------");
		System.err.println("Vendas: " + vendasJson);
		System.out.println("---------------------------------------------------------------------------------------");
		//System.err.println("Vendedores: " + forcaVendasJson);

		var rClientes = integrationApiClient.postClientes(clientesJson);
		var rEstoque = integrationApiClient.postEstoque(estoqueJson);
		var rProdutos = integrationApiClient.postProdutos(produtosJson);
		var rVendas = integrationApiClient.postVendas(vendasJson);
		var rForca = integrationApiClient.postForcaVendas(forcaVendasJson);

		System.out.println("POST /clientes -> " + rClientes.status() + " success=" + rClientes.success());
		System.out.println("POST /estoque  -> " + rEstoque.status() + " success=" + rEstoque.success());
		System.out.println("POST /produtos -> " + rProdutos.status() + " success=" + rProdutos.success());
		System.out.println("POST /vendas   -> " + rVendas.status() + " success=" + rVendas.success());
		System.out.println("POST /forca-vendas -> " + rForca.status() + " success=" + rForca.success());

	}

	private Optional<Path> findMaisRecente(Path dir, String prefixo) throws IOException {
		if (dir == null || !Files.isDirectory(dir)) {
			throw new IOException("Diretório inválido: " + dir);
		}

		try (Stream<Path> s = Files.list(dir)) {
			return s.filter(Files::isRegularFile).filter(p -> {
				String name = p.getFileName().toString().toUpperCase();
				return name.startsWith(prefixo.toUpperCase()) && name.endsWith(".TXT");
			}).map(p -> new FileCandidate(p, safeLastModified(p)))
					.max(Comparator.comparing(FileCandidate::lastModified)).map(FileCandidate::path);
		}
	}

	private FileTime safeLastModified(Path p) {
		try {
			return Files.getLastModifiedTime(p);
		} catch (IOException e) {
			return FileTime.fromMillis(0);
		}
	}

	private record FileCandidate(Path path, FileTime lastModified) {
	}
}

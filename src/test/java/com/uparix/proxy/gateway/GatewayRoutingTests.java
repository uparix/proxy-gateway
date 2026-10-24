package com.uparix.proxy.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Sends requests through the running gateway to a local stub backend.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutingTests {

	private static final List<RecordedRequest> received = new CopyOnWriteArrayList<>();

	private static final HttpServer backend = startBackend();

	@LocalServerPort
	private int port;

	private WebTestClient client;

	@DynamicPropertySource
	static void routeToBackend(DynamicPropertyRegistry registry) {
		String prefix = "spring.cloud.gateway.server.webflux.routes[0].";
		registry.add(prefix + "id", () -> "backend");
		registry.add(prefix + "uri", () -> "http://localhost:" + backend.getAddress().getPort());
		registry.add(prefix + "predicates[0]", () -> "Path=/api/v1/proxyOne/**");
	}

	@AfterAll
	static void stopBackend() {
		backend.stop(0);
	}

	@BeforeEach
	void setUp() {
		received.clear();
		client = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
	}

	@Test
	void forwardsMatchingRequestWithPathAndQuery() {
		client.get().uri("/api/v1/proxyOne/items?page=2")
				.header("X-Test", "hello")
				.exchange()
				.expectStatus().isOk()
				.expectBody(String.class).isEqualTo("backend:/api/v1/proxyOne/items?page=2");

		assertThat(received).singleElement().satisfies(request -> {
			assertThat(request.method()).isEqualTo("GET");
			assertThat(request.testHeader()).isEqualTo("hello");
		});
	}

	@Test
	void forwardsRequestBody() {
		client.post().uri("/api/v1/proxyOne/items")
				.bodyValue("payload")
				.exchange()
				.expectStatus().isOk();

		assertThat(received).singleElement().satisfies(request -> {
			assertThat(request.method()).isEqualTo("POST");
			assertThat(request.body()).isEqualTo("payload");
		});
	}

	@Test
	void returnsNotFoundForUnmatchedPath() {
		client.get().uri("/api/v1/unknown")
				.exchange()
				.expectStatus().isNotFound();

		assertThat(received).isEmpty();
	}

	private static HttpServer startBackend() {
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
			server.createContext("/", GatewayRoutingTests::handle);
			server.start();
			return server;
		}
		catch (IOException ex) {
			throw new UncheckedIOException(ex);
		}
	}

	private static void handle(HttpExchange exchange) throws IOException {
		String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
		received.add(new RecordedRequest(exchange.getRequestMethod(),
				exchange.getRequestHeaders().getFirst("X-Test"), body));

		byte[] response = ("backend:" + exchange.getRequestURI()).getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(200, response.length);
		exchange.getResponseBody().write(response);
		exchange.close();
	}

	private record RecordedRequest(String method, String testHeader, String body) {
	}

}

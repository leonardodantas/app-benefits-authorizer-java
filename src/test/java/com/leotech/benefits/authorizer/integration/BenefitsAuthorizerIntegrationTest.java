package com.leotech.benefits.authorizer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leotech.benefits.authorizer.api.requests.CreateCardRequest;
import com.leotech.benefits.authorizer.api.requests.CreateTransactionRequest;
import com.leotech.benefits.authorizer.api.responses.CreateCardResponse;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class BenefitsAuthorizerIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("miniautorizador")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    private PostResult postRaw(final String path, final Object request) {
        try {
            final String json = objectMapper.writeValueAsString(request);
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            final HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return new PostResult(httpResponse.statusCode(), httpResponse.body());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private record PostResult(int status, String body) {
    }

    private RecordGetResult getRaw(final String path) {
        try {
            final HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .GET()
                    .build();
            final HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return new RecordGetResult(httpResponse.statusCode(), httpResponse.body());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private record RecordGetResult(int status, String body) {
    }

    @Nested
    @DisplayName("POST /cartoes")
    class CreateCard {

        @Test
        @DisplayName("should return 201 when card is created")
        void shouldReturn201() throws Exception {
            final var request = new CreateCardRequest("1111111111111111", "1234");
            final var result = postRaw("/cartoes", request);

            assertThat(result.status()).isEqualTo(201);

            final var response = objectMapper.readValue(result.body(), CreateCardResponse.class);
            assertThat(response.cardNumber()).isEqualTo("1111111111111111");
        }

        @Test
        @DisplayName("should return 422 when card already exists")
        void shouldReturn422() {
            final var request = new CreateCardRequest("2222222222222222", "1234");
            postRaw("/cartoes", request);

            final var result = postRaw("/cartoes", request);

            assertThat(result.status()).isEqualTo(422);
        }

        @Test
        @DisplayName("should return 400 when card number has invalid format")
        void shouldReturn400InvalidCardNumber() {
            final var request = new CreateCardRequest("123", "1234");
            final var result = postRaw("/cartoes", request);

            assertThat(result.status()).isEqualTo(400);
        }

        @Test
        @DisplayName("should return 400 when campos ausentes")
        void shouldReturn400MissingFields() {
            final var result = postRaw("/cartoes", new CreateCardRequest("", ""));

            assertThat(result.status()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("GET /cartoes/{numeroCartao}")
    class GetBalance {

        private static final String CARD_NUMBER = "3333333333333333";

        @BeforeEach
        void createCard() {
            final var request = new CreateCardRequest(CARD_NUMBER, "1234");
            postRaw("/cartoes", request);
        }

        @Test
        @DisplayName("should return 200 and balance when card exists")
        void shouldReturn200() {
            final var result = getRaw("/cartoes/" + CARD_NUMBER);

            assertThat(result.status()).isEqualTo(200);
            assertThat(new BigDecimal(result.body())).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("should return 404 when card does not exist")
        void shouldReturn404() {
            final var result = getRaw("/cartoes/999");

            assertThat(result.status()).isEqualTo(404);
        }
    }

    @Nested
    @DisplayName("POST /transacoes")
    class CreateTransaction {

        private static final String CARD_NUMBER = "4444444444444444";

        @BeforeEach
        void createCard() {
            final var request = new CreateCardRequest(CARD_NUMBER, "1234");
            postRaw("/cartoes", request);
        }

        @Test
        @DisplayName("should return 201 with OK when transaction succeeds")
        void shouldReturn201() {
            final var request = new CreateTransactionRequest(CARD_NUMBER, "1234", new BigDecimal("50.00"));
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(201);
        }

        @Test
        @DisplayName("should return 422 with CARTAO_INEXISTENTE when card does not exist")
        void shouldReturn422CardNotExists() {
            final var request = new CreateTransactionRequest("9999999999999999", "1234", new BigDecimal("10.00"));
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(422);
            assertThat(result.body()).isEqualTo("CARTAO_INEXISTENTE");
        }

        @Test
        @DisplayName("should return 422 with SENHA_INVALIDA when password is wrong")
        void shouldReturn422InvalidPassword() {
            final var request = new CreateTransactionRequest(CARD_NUMBER, "wrong", new BigDecimal("10.00"));
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(422);
            assertThat(result.body()).isEqualTo("SENHA_INVALIDA");
        }

        @Test
        @DisplayName("should return 422 with SALDO_INSUFICIENTE when balance is insufficient")
        void shouldReturn422InsufficientBalance() {
            final var request = new CreateTransactionRequest(CARD_NUMBER, "1234", new BigDecimal("99999"));
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(422);
            assertThat(result.body()).isEqualTo("SALDO_INSUFICIENTE");
        }

        @Test
        @DisplayName("should return 400 when card number has invalid format")
        void shouldReturn400InvalidCardNumber() {
            final var request = new CreateTransactionRequest("123", "1234", new BigDecimal("10.00"));
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(400);
        }

        @Test
        @DisplayName("should return 400 when amount is zero")
        void shouldReturn400InvalidAmount() {
            final var request = new CreateTransactionRequest(CARD_NUMBER, "1234", BigDecimal.ZERO);
            final var result = postRaw("/transacoes", request);

            assertThat(result.status()).isEqualTo(400);
        }
    }

    @Nested
    @DisplayName("GET /transacoes/{numeroCartao}")
    class GetTransactionHistory {

        private static final String CARD_NUMBER = "7777777777777777";

        @BeforeEach
        void createCardAndTransaction() {
            postRaw("/cartoes", new CreateCardRequest(CARD_NUMBER, "1234"));
            postRaw("/transacoes", new CreateTransactionRequest(CARD_NUMBER, "1234", new BigDecimal("50.00")));
        }

        @Test
        @DisplayName("should return 200 with paginated history and success status")
        void shouldReturn200() {
            final var result = getRaw("/transacoes/" + CARD_NUMBER + "?page=0&size=20");

            assertThat(result.status()).isEqualTo(200);
            assertThat(result.body()).contains("\"status\":\"SUCCESS\"");
            assertThat(result.body()).contains("\"mensagem\":\"TRANSACAO_APROVADA\"");
            assertThat(result.body()).contains("numeroCartao");
            assertThat(result.body()).contains("saldoAnterior");
            assertThat(result.body()).contains("novoSaldo");
            assertThat(result.body()).contains("valor");
            assertThat(result.body()).contains("dataHora");
        }

        @Test
        @DisplayName("should return 200 with empty content when card has no transactions")
        void shouldReturn200Empty() {
            final var result = getRaw("/transacoes/8888888888888888" + "?page=0&size=20");

            assertThat(result.status()).isEqualTo(200);
            assertThat(result.body()).contains("\"content\":[]");
        }
    }

    @Nested
    @DisplayName("Error transactions")
    class ErrorTransaction {

        private static final String CARD_NUMBER = "9999999999999999";

        @BeforeEach
        void createCardAndFailedTransaction() {
            postRaw("/cartoes", new CreateCardRequest(CARD_NUMBER, "1234"));
            postRaw("/transacoes", new CreateTransactionRequest(CARD_NUMBER, "wrong", new BigDecimal("10.00")));
        }

        @Test
        @DisplayName("should persist error event in history")
        void shouldPersistErrorEvent() {
            final var result = getRaw("/transacoes/" + CARD_NUMBER + "?page=0&size=20");

            assertThat(result.status()).isEqualTo(200);
            assertThat(result.body()).contains("\"status\":\"ERROR\"");
            assertThat(result.body()).contains("\"mensagem\":\"SENHA_INVALIDA\"");
            assertThat(result.body()).contains("\"saldoAnterior\":null");
            assertThat(result.body()).contains("\"novoSaldo\":null");
        }
    }

    @Nested
    @DisplayName("Concurrency")
    class Concurrency {

        private static final String TEN_TRANSACTIONS_CARD = "5555555555555555";
        private static final String THREE_DIFFERENT_AMOUNTS_CARD = "6666666666666666";
        private static final int THREAD_COUNT = 10;
        private static final BigDecimal AMOUNT = new BigDecimal("30.00");
        private static final BigDecimal EXPECTED_BALANCE = new BigDecimal("200.00");

        @Test
        @DisplayName("should handle concurrent transactions with correct final balance")
        void shouldHandleConcurrentTransactions() throws Exception {
            postRaw("/cartoes", new CreateCardRequest(TEN_TRANSACTIONS_CARD, "1234"));

            final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            final CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT);
            final var request = new CreateTransactionRequest(TEN_TRANSACTIONS_CARD, "1234", AMOUNT);

            final List<Callable<PostResult>> tasks = new ArrayList<>();
            for (int i = 0; i < THREAD_COUNT; i++) {
                tasks.add(() -> {
                    barrier.await(10, TimeUnit.SECONDS);
                    return postRaw("/transacoes", request);
                });
            }

            final List<Future<PostResult>> futures = executor.invokeAll(tasks, 30, TimeUnit.SECONDS);
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

            for (final var future : futures) {
                final var result = future.get();
                assertThat(result.status()).as("body: " + result.body()).isEqualTo(201);
            }

            final var balanceResult = getRaw("/cartoes/" + TEN_TRANSACTIONS_CARD);
            assertThat(balanceResult.status()).isEqualTo(200);
            assertThat(new BigDecimal(balanceResult.body())).isEqualByComparingTo(EXPECTED_BALANCE);
        }

        @Test
        @DisplayName("should handle concurrent transactions with different amounts: 2 succeed, 1 fails with insufficient balance")
        void shouldHandleConcurrentTransactionsWithDifferentAmounts() throws Exception {
            postRaw("/cartoes", new CreateCardRequest(THREE_DIFFERENT_AMOUNTS_CARD, "1234"));

            final int threadCount = 3;
            final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            final CyclicBarrier barrier = new CyclicBarrier(threadCount);

            final var amounts = List.of(
                    new BigDecimal("200.00"),
                    new BigDecimal("300.00"),
                    new BigDecimal("100.00")
            );

            final List<Callable<PostResult>> tasks = new ArrayList<>();
            for (final var amount : amounts) {
                tasks.add(() -> {
                    barrier.await(10, TimeUnit.SECONDS);
                    return postRaw("/transacoes", new CreateTransactionRequest(THREE_DIFFERENT_AMOUNTS_CARD, "1234", amount));
                });
            }

            final List<Future<PostResult>> futures = executor.invokeAll(tasks, 30, TimeUnit.SECONDS);
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);

            long successCount = 0;
            long failCount = 0;
            for (final var future : futures) {
                final var result = future.get();
                if (result.status() == 201) {
                    successCount++;
                } else if (result.status() == 422 && result.body().equals("SALDO_INSUFICIENTE")) {
                    failCount++;
                }
            }

            assertThat(successCount).isEqualTo(2);
            assertThat(failCount).isEqualTo(1);
        }
    }

}

package com.microsoft.japan.fizzbuzz.adapter.in.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 保存に失敗した場合の REST API の結合テスト。
 * Repository を失敗させるため、他の結合テストとは Spring アプリケーションコンテキストを分ける。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FizzBuzzApiSaveFailureIT {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort
    private int port;

    @MockitoBean
    private FizzBuzzRepository repository;

    /**
     * 保存に失敗した場合、成功を返さず HTTP 500 の Problem Details が返ることを確認する。
     */
    @Test
    @DisplayName("保存に失敗した場合HTTP500のProblemDetailsを返す")
    void 保存に失敗した場合HTTP500のProblemDetailsを返す() throws Exception {
        Mockito.doThrow(new DataAccessResourceFailureException("unavailable")).when(repository)
                .save(Mockito.any(FizzBuzz.class));

        final var response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/fizzbuzz"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"number\":1}")).build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElseThrow()
                .startsWith("application/problem+json"));
        assertEquals("The FizzBuzz results could not be saved.",
                JSON.readTree(response.body()).get("detail").asString());
    }
}

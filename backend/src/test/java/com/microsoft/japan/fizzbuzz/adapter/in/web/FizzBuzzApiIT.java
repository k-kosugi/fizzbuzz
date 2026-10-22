package com.microsoft.japan.fizzbuzz.adapter.in.web;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.microsoft.japan.fizzbuzz.adapter.out.persistence.InMemoryFizzBuzzRepository;
import com.microsoft.japan.fizzbuzz.adapter.out.persistence.InMemoryFizzBuzzRepository.Row;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FizzBuzz REST API の結合テスト。
 * 実際の HTTP サーバーとメモリ上の Repository を使用して、入力検証から保存・取得までを検証する。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FizzBuzzApiIT {

    private static final JsonMapper JSON = JsonMapper.builder().build();
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @LocalServerPort
    private int port;

    @Autowired
    private InMemoryFizzBuzzRepository repository;

    /**
     * テストごとに独立させるため、保存済みのデータを削除する。
     */
    @BeforeEach
    void clearRepository() {
        repository.deleteAll();
    }

    /**
     * number の境界値 (1) の前後を検証する。0 は HTTP 400、1 と 2 は HTTP 201 になる。
     */
    @Test
    @DisplayName("numberの境界値前後が渡された場合境界に応じた結果になる")
    void numberの境界値前後が渡された場合境界に応じた結果になる() throws Exception {
        assertEquals(400, post("{\"number\":0}").statusCode());
        assertEquals(201, post("{\"number\":1}").statusCode());
        assertEquals(201, post("{\"number\":2}").statusCode());
    }

    /**
     * int の最大値 (2147483647) とその直後を検証する。最大値は HTTP 201、直後は HTTP 400 になる。
     */
    @Test
    @DisplayName("numberの境界値としてintの最大値前後が渡された場合境界に応じた結果になる")
    void numberの境界値としてintの最大値前後が渡された場合境界に応じた結果になる() throws Exception {
        assertEquals(201, post("{\"number\":2147483647}").statusCode());
        assertEquals(400, post("{\"number\":2147483648}").statusCode());
    }

    @Test
    @DisplayName("numberに15が渡された場合HTTP201で数値とFizzBuzzを返す")
    void numberに15が渡された場合HTTP201で数値とFizzBuzzを返す() throws Exception {
        final var response = post("{\"number\":15}");

        assertEquals(201, response.statusCode());
        assertEquals(JSON.readTree("{\"number\":15,\"value\":\"FizzBuzz\"}"), JSON.readTree(response.body()));
    }

    @Test
    @DisplayName("numberに15が渡された場合Locationヘッダーに取得先を返す")
    void numberに15が渡された場合Locationヘッダーに取得先を返す() throws Exception {
        final var response = post("{\"number\":15}");

        assertEquals(endpoint("/api/fizzbuzz/15").toString(), response.headers().firstValue("Location").orElseThrow());
    }

    @Test
    @DisplayName("numberに3が渡された場合isFizzだけtrueの行を保存する")
    void numberに3が渡された場合isFizzだけtrueの行を保存する() throws Exception {
        post("{\"number\":3}");

        assertEquals(List.of(new Row(3, true, false, false, "Fizz")), repository.findAllRows());
    }

    @Test
    @DisplayName("numberに5が渡された場合isBuzzだけtrueの行を保存する")
    void numberに5が渡された場合isBuzzだけtrueの行を保存する() throws Exception {
        post("{\"number\":5}");

        assertEquals(List.of(new Row(5, false, true, false, "Buzz")), repository.findAllRows());
    }

    @Test
    @DisplayName("numberに15が渡された場合すべてtrueの行を保存する")
    void numberに15が渡された場合すべてtrueの行を保存する() throws Exception {
        post("{\"number\":15}");

        assertEquals(List.of(new Row(15, true, true, true, "FizzBuzz")), repository.findAllRows());
    }

    @Test
    @DisplayName("numberに1が渡された場合すべてfalseで数値の文字列の行を保存する")
    void numberに1が渡された場合すべてfalseで数値の文字列の行を保存する() throws Exception {
        post("{\"number\":1}");

        assertEquals(List.of(new Row(1, false, false, false, "1")), repository.findAllRows());
    }

    @Test
    @DisplayName("numberに1が2回渡された場合1件のまま保存する")
    void numberに1が2回渡された場合1件のまま保存する() throws Exception {
        post("{\"number\":1}");
        final var second = post("{\"number\":1}");

        assertEquals(201, second.statusCode());
        assertEquals(1, repository.findAllRows().size());
    }

    @Test
    @DisplayName("異なるnumberが続けて渡された場合それぞれ保存する")
    void 異なるnumberが続けて渡された場合それぞれ保存する() throws Exception {
        post("{\"number\":2}");
        post("{\"number\":1}");

        assertEquals(List.of(1, 2), repository.findAllRows().stream().map(Row::number).toList());
    }

    /**
     * number に不正な値が渡された場合、HTTP 400 の Problem Details が返ることを確認する。
     */
    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"number\":null}", "{\"number\":-1}", "{\"number\":1.5}",
            "{\"number\":1.0}", "{\"number\":\"1\"}", "{\"number\":true}", "{\"number\":[]}",
            "{\"from\":1,\"to\":5}", "{\"number\":", "null", "[]", ""})
    @DisplayName("numberに不正な値が渡された場合HTTP400のProblemDetailsを返す")
    void numberに不正な値が渡された場合HTTP400のProblemDetailsを返す(final String body) throws Exception {
        final var response = post(body);

        assertEquals(400, response.statusCode());
        assertProblem(response, 400, "/api/fizzbuzz");
    }

    @Test
    @DisplayName("numberに0が渡された場合保存しない")
    void numberに0が渡された場合保存しない() throws Exception {
        post("{\"number\":0}");

        assertTrue(repository.findAllRows().isEmpty());
    }

    @Test
    @DisplayName("保存済みの3を取得した場合HTTP200で数値とFizzを返す")
    void 保存済みの3を取得した場合HTTP200で数値とFizzを返す() throws Exception {
        post("{\"number\":3}");

        final var response = get("/api/fizzbuzz/3");

        assertEquals(200, response.statusCode());
        assertEquals(JSON.readTree("{\"number\":3,\"value\":\"Fizz\"}"), JSON.readTree(response.body()));
    }

    @Test
    @DisplayName("保存済みの3の隣の4を取得した場合HTTP404のProblemDetailsを返す")
    void 保存済みの3の隣の4を取得した場合HTTP404のProblemDetailsを返す() throws Exception {
        post("{\"number\":3}");

        final var response = get("/api/fizzbuzz/4");

        assertEquals(404, response.statusCode());
        assertProblem(response, 404, "/api/fizzbuzz/4");
    }

    @Test
    @DisplayName("何も保存していない状態で1を取得した場合HTTP404を返す")
    void 何も保存していない状態で1を取得した場合HTTP404を返す() throws Exception {
        assertEquals(404, get("/api/fizzbuzz/1").statusCode());
    }

    @Test
    @DisplayName("numberに0を指定して取得した場合HTTP404を返す")
    void numberに0を指定して取得した場合HTTP404を返す() throws Exception {
        assertEquals(404, get("/api/fizzbuzz/0").statusCode());
    }

    @Test
    @DisplayName("numberに整数以外を指定して取得した場合HTTP400のProblemDetailsを返す")
    void numberに整数以外を指定して取得した場合HTTP400のProblemDetailsを返す() throws Exception {
        final var response = get("/api/fizzbuzz/abc");

        assertEquals(400, response.statusCode());
        assertProblem(response, 400, "/api/fizzbuzz/abc");
    }

    /**
     * 未対応の HTTP メソッド、リクエスト形式、レスポンス形式が指定された場合に、対応するエラーが返ることを確認する。
     */
    @ParameterizedTest
    @CsvSource({"GET,application/json,application/json,405", "POST,text/plain,application/json,415",
            "POST,application/json,text/plain,406"})
    @DisplayName("未対応のHTTP契約が渡された場合対応するエラーを返す")
    void 未対応のHTTP契約が渡された場合対応するエラーを返す(final String method, final String contentType,
            final String accept, final int expectedStatus) throws Exception {
        final var request = HttpRequest.newBuilder(endpoint("/api/fizzbuzz"))
                .header("Content-Type", contentType).header("Accept", accept)
                .method(method, HttpRequest.BodyPublishers.ofString("{\"number\":1}")).build();

        assertEquals(expectedStatus, HTTP.send(request, HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    /**
     * OpenAPI 仕様が公開されていることを確認する。
     */
    @Test
    @DisplayName("OpenAPI仕様が要求された場合HTTP200で返す")
    void OpenAPI仕様が要求された場合HTTP200で返す() throws Exception {
        final var response = get("/openapi.yaml");

        assertTrue(response.statusCode() == 200 && response.body().contains("/api/fizzbuzz/{number}:"));
    }

    private HttpResponse<String> post(final String body) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(endpoint("/api/fizzbuzz"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(final String path) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(endpoint(path)).GET().build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI endpoint(final String path) {
        return URI.create("http://localhost:" + port + path);
    }

    private void assertProblem(final HttpResponse<String> response, final int status, final String instance) {
        assertTrue(response.headers().firstValue("Content-Type").orElseThrow()
                .startsWith("application/problem+json"));
        final JsonNode problem = JSON.readTree(response.body());
        assertEquals(status, problem.get("status").asInt());
        assertEquals(instance, problem.get("instance").asString());
        assertTrue(problem.has("title") && problem.has("detail"), response.body());
    }
}

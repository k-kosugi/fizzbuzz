package com.microsoft.japan.fizzbuzz;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Kubernetes のプローブ用ヘルスチェックの結合テスト。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthProbeIT {

    private static final HttpClient HTTP = HttpClient.newHttpClient();
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @LocalServerPort
    private int port;

    @ParameterizedTest
    @ValueSource(strings = {"liveness", "readiness"})
    @DisplayName("起動済みの場合LivenessとReadinessがHTTP200でUPを返す")
    void 起動済みの場合LivenessとReadinessがHTTP200でUPを返す(final String probe) throws Exception {
        final var response = get("/actuator/health/" + probe);

        assertEquals(200, response.statusCode());
        assertEquals("UP", JSON.readTree(response.body()).get("status").asString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"env", "beans", "metrics", "heapdump"})
    @DisplayName("health以外のactuatorエンドポイントが要求された場合HTTP404を返す")
    void health以外のactuatorエンドポイントが要求された場合HTTP404を返す(final String endpoint) throws Exception {
        assertEquals(404, get("/actuator/" + endpoint).statusCode());
    }

    private HttpResponse<String> get(final String path) throws Exception {
        return HTTP.send(HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }
}

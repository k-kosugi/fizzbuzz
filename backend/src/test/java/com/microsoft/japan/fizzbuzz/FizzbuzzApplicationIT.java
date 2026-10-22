package com.microsoft.japan.fizzbuzz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * アプリケーション全体の起動を確認する結合テスト。
 */
@SpringBootTest
class FizzbuzzApplicationIT {

    /**
     * アプリケーションコンテキストが正常に起動することを確認する。
     */
    @Test
    @DisplayName("アプリケーションコンテキストが起動する")
    void contextLoads() {
    }
}

package com.microsoft.japan.fizzbuzz.domain;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FizzBuzzRange (範囲の Value Object) の単体テスト。
 */
class FizzBuzzRangeTest {

    /**
     * from の境界値 (1) の前後を検証する。
     * 0 は不正、1 と 2 は正常。型の下限 (int の最小値) も不正になる。
     */
    @Test
    @DisplayName("fromの境界値前後が渡された場合境界に応じた結果になる")
    void fromの境界値前後が渡された場合境界に応じた結果になる() {
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(Integer.MIN_VALUE, 100));
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(0, 100));
        assertDoesNotThrow(() -> new FizzBuzzRange(1, 100));
        assertDoesNotThrow(() -> new FizzBuzzRange(2, 100));
    }

    /**
     * to の境界値 (from と同じ値) の前後を検証する。
     * from が 5 の場合、to が 4 は不正、5 と 6 は正常。
     */
    @Test
    @DisplayName("toの境界値前後が渡された場合境界に応じた結果になる")
    void toの境界値前後が渡された場合境界に応じた結果になる() {
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(5, 4));
        assertDoesNotThrow(() -> new FizzBuzzRange(5, 5));
        assertDoesNotThrow(() -> new FizzBuzzRange(5, 6));
    }

    /**
     * 範囲の個数の境界値 (MAX_SIZE) の前後を検証する。
     * 個数が 999 と 1000 は正常、1001 は不正。
     */
    @Test
    @DisplayName("範囲の個数の境界値前後が渡された場合境界に応じた結果になる")
    void 範囲の個数の境界値前後が渡された場合境界に応じた結果になる() {
        assertDoesNotThrow(() -> new FizzBuzzRange(1, FizzBuzzRange.MAX_SIZE - 1));
        assertDoesNotThrow(() -> new FizzBuzzRange(1, FizzBuzzRange.MAX_SIZE));
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(1, FizzBuzzRange.MAX_SIZE + 1));
    }

    /**
     * 型の上限 (int の最大値) の前後を検証する。
     * 上限付近でもオーバーフローせず、個数が正しく数えられる。
     * 個数が上限を超える範囲 (1 から int の最大値) は不正になる。
     */
    @Test
    @DisplayName("intの最大値の境界値前後が渡された場合境界に応じた結果になる")
    void intの最大値の境界値前後が渡された場合境界に応じた結果になる() {
        assertEquals(2, new FizzBuzzRange(Integer.MAX_VALUE - 1, Integer.MAX_VALUE).fizzBuzzes().size());
        assertEquals(1, new FizzBuzzRange(Integer.MAX_VALUE, Integer.MAX_VALUE).fizzBuzzes().size());
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(1, Integer.MAX_VALUE));
    }

    /**
     * from が 1 未満の場合 (同値分割: 負数の中央付近) 、InvalidFizzBuzzRangeException が発生することを確認する。
     */
    @Test
    @DisplayName("fromに負数が渡された場合InvalidFizzBuzzRangeExceptionが発生する")
    void fromに負数が渡された場合InvalidFizzBuzzRangeExceptionが発生する() {
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(-1073741824, 100));
    }

    /**
     * to が from より小さい場合 (同値分割) 、InvalidFizzBuzzRangeException が発生することを確認する。
     */
    @Test
    @DisplayName("toがfromより小さい値の場合InvalidFizzBuzzRangeExceptionが発生する")
    void toがfromより小さい値の場合InvalidFizzBuzzRangeExceptionが発生する() {
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(500, 250));
    }

    /**
     * 範囲の個数が上限を超える場合 (同値分割) 、InvalidFizzBuzzRangeException が発生することを確認する。
     */
    @Test
    @DisplayName("範囲の個数が上限を超える場合InvalidFizzBuzzRangeExceptionが発生する")
    void 範囲の個数が上限を超える場合InvalidFizzBuzzRangeExceptionが発生する() {
        assertThrows(InvalidFizzBuzzRangeException.class, () -> new FizzBuzzRange(1, 5000));
    }

    /**
     * 一般的な範囲 (1 から 100) で、from から to までの数値が昇順で生成されることを確認する。
     */
    @Test
    @DisplayName("fromに1toに100が渡された場合1から100までを昇順で返す")
    void fromに1toに100が渡された場合1から100までを昇順で返す() {
        final var numbers = new FizzBuzzRange(1, 100).fizzBuzzes().stream().map(FizzBuzz::getValue).toList();
        assertEquals(IntStream.rangeClosed(1, 100).boxed().toList(), numbers);
    }

    /**
     * 一般的な範囲 (1 から 15) で、各 FizzBuzz の文字列が規則どおりであることを確認する。
     */
    @Test
    @DisplayName("fromに1toに15が渡された場合FizzBuzzの規則どおりの文字列を返す")
    void fromに1toに15が渡された場合FizzBuzzの規則どおりの文字列を返す() {
        final var results = new FizzBuzzRange(1, 15).fizzBuzzes().stream().map(FizzBuzz::toString).toList();
        assertEquals(List.of("1", "2", "Fizz", "4", "Buzz", "Fizz", "7", "8", "Fizz", "Buzz",
                "11", "Fizz", "13", "14", "FizzBuzz"), results);
    }
}

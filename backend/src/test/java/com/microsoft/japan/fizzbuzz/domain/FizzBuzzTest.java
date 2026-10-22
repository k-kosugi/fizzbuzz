package com.microsoft.japan.fizzbuzz.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * FizzBuzz ドメインモデルの単体テスト。
 */
class FizzBuzzTest {

    /**
     * コンストラクタに 1 以上の値が渡された場合、例外が発生しないことを確認する。
     * 境界値 (1)、中央付近の値、int の最大値を検証する。
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 1073741823, Integer.MAX_VALUE - 1, Integer.MAX_VALUE})
    @DisplayName("valueに1以上の値が渡された場合正常終了する")
    void valueに1以上の値が渡された場合正常終了する(final int value) {
        assertDoesNotThrow(() -> new FizzBuzz(value));
    }

    /**
     * コンストラクタに 0 以下の値が渡された場合、IllegalArgumentException が発生することを確認する。
     * 境界値 (0)、境界値の手前 (-1)、中央付近の負数、int の最小値を検証する。
     */
    @ParameterizedTest
    @ValueSource(ints = {0, -1, -1073741824, Integer.MIN_VALUE})
    @DisplayName("valueに0以下の値が渡された場合IllegalArgumentExceptionが発生する")
    void valueに0以下の値が渡された場合IllegalArgumentExceptionが発生する(final int value) {
        assertThrows(IllegalArgumentException.class, () -> new FizzBuzz(value));
    }

    /**
     * 3 の倍数の場合、isFizz が true を返すことを確認する。
     * 15 の倍数 (Fizz と Buzz の両方に該当する値) も true になる。
     */
    @ParameterizedTest
    @ValueSource(ints = {3, 6, 9, 15, 30, Integer.MAX_VALUE - 1})
    @DisplayName("valueに3の倍数が渡された場合isFizzがtrueを返す")
    void valueに3の倍数が渡された場合isFizzがtrueを返す(final int value) {
        assertTrue(new FizzBuzz(value).isFizz());
    }

    /**
     * 3 の倍数でない場合、isFizz が false を返すことを確認する。
     * 3 の手前 (2) と直後 (4)、5 の倍数、int の最大値を検証する。
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 5, 10, Integer.MAX_VALUE})
    @DisplayName("valueに3の倍数でない値が渡された場合isFizzがfalseを返す")
    void valueに3の倍数でない値が渡された場合isFizzがfalseを返す(final int value) {
        assertFalse(new FizzBuzz(value).isFizz());
    }

    /**
     * 5 の倍数の場合、isBuzz が true を返すことを確認する。
     * 15 の倍数 (Fizz と Buzz の両方に該当する値) も true になる。
     */
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15, 20, 30, Integer.MAX_VALUE - 2})
    @DisplayName("valueに5の倍数が渡された場合isBuzzがtrueを返す")
    void valueに5の倍数が渡された場合isBuzzがtrueを返す(final int value) {
        assertTrue(new FizzBuzz(value).isBuzz());
    }

    /**
     * 5 の倍数でない場合、isBuzz が false を返すことを確認する。
     * 5 の手前 (4) と直後 (6)、3 の倍数、int の最大値を検証する。
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 4, 6, 9, Integer.MAX_VALUE})
    @DisplayName("valueに5の倍数でない値が渡された場合isBuzzがfalseを返す")
    void valueに5の倍数でない値が渡された場合isBuzzがfalseを返す(final int value) {
        assertFalse(new FizzBuzz(value).isBuzz());
    }

    /**
     * 15 の倍数の場合、isFizzBuzz が true を返すことを確認する。
     */
    @ParameterizedTest
    @ValueSource(ints = {15, 30, 45, 90, Integer.MAX_VALUE - 7})
    @DisplayName("valueに15の倍数が渡された場合isFizzBuzzがtrueを返す")
    void valueに15の倍数が渡された場合isFizzBuzzがtrueを返す(final int value) {
        assertTrue(new FizzBuzz(value).isFizzBuzz());
    }

    /**
     * 15 の倍数でない場合、isFizzBuzz が false を返すことを確認する。
     * 3 のみ・5 のみの倍数、15 の手前 (14) と直後 (16) を検証する。
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 14, 16, Integer.MAX_VALUE})
    @DisplayName("valueに15の倍数でない値が渡された場合isFizzBuzzがfalseを返す")
    void valueに15の倍数でない値が渡された場合isFizzBuzzがfalseを返す(final int value) {
        assertFalse(new FizzBuzz(value).isFizzBuzz());
    }

    /**
     * 15 の倍数の場合、toString が FizzBuzz を返すことを確認する。
     */
    @ParameterizedTest
    @ValueSource(ints = {15, 30, 45, 90})
    @DisplayName("valueに15の倍数が渡された場合toStringがFizzBuzzを返す")
    void valueに15の倍数が渡された場合toStringがFizzBuzzを返す(final int value) {
        assertEquals("FizzBuzz", new FizzBuzz(value).toString());
    }

    /**
     * 3 の倍数のみ (5 の倍数ではない) の場合、toString が Fizz を返すことを確認する。
     */
    @ParameterizedTest
    @ValueSource(ints = {3, 6, 9, 12, 18, Integer.MAX_VALUE - 1})
    @DisplayName("valueに3の倍数のみが渡された場合toStringがFizzを返す")
    void valueに3の倍数のみが渡された場合toStringがFizzを返す(final int value) {
        assertEquals("Fizz", new FizzBuzz(value).toString());
    }

    /**
     * 5 の倍数のみ (3 の倍数ではない) の場合、toString が Buzz を返すことを確認する。
     */
    @ParameterizedTest
    @ValueSource(ints = {5, 10, 20, 25, 35, Integer.MAX_VALUE - 2})
    @DisplayName("valueに5の倍数のみが渡された場合toStringがBuzzを返す")
    void valueに5の倍数のみが渡された場合toStringがBuzzを返す(final int value) {
        assertEquals("Buzz", new FizzBuzz(value).toString());
    }

    /**
     * 3 の倍数でも 5 の倍数でもない場合、toString が数値の文字列を返すことを確認する。
     * 境界値 (1)、int の最大値を含む。
     */
    @ParameterizedTest
    @CsvSource({"1,1", "2,2", "4,4", "7,7", "14,14", "16,16", "2147483647,2147483647"})
    @DisplayName("valueに3と5の倍数でない値が渡された場合toStringが数値の文字列を返す")
    void valueに3と5の倍数でない値が渡された場合toStringが数値の文字列を返す(final int value, final String expected) {
        assertEquals(expected, new FizzBuzz(value).toString());
    }

    /**
     * 識別子である数値を getValue で取得できることを確認する。
     */
    @Test
    @DisplayName("valueに15が渡された場合getValueが15を返す")
    void valueに15が渡された場合getValueが15を返す() {
        assertEquals(15, new FizzBuzz(15).getValue());
    }

    /**
     * 同じ数値の FizzBuzz は同一の Entity とみなされることを確認する。
     */
    @Test
    @DisplayName("同じ数値の場合equalsがtrueを返す")
    void 同じ数値の場合equalsがtrueを返す() {
        assertEquals(new FizzBuzz(15), new FizzBuzz(15));
    }

    /**
     * 異なる数値の FizzBuzz は別の Entity とみなされることを確認する。
     */
    @Test
    @DisplayName("異なる数値の場合equalsがfalseを返す")
    void 異なる数値の場合equalsがfalseを返す() {
        assertNotEquals(new FizzBuzz(15), new FizzBuzz(14));
    }

    /**
     * 同じ参照との比較で equals が true を返すことを確認する。
     */
    @Test
    @DisplayName("同じ参照の場合equalsがtrueを返す")
    void 同じ参照の場合equalsがtrueを返す() {
        final var fizzBuzz = new FizzBuzz(15);
        assertEquals(fizzBuzz, fizzBuzz);
    }

    /**
     * null との比較で equals が false を返すことを確認する。
     */
    @Test
    @DisplayName("nullが渡された場合equalsがfalseを返す")
    void nullが渡された場合equalsがfalseを返す() {
        assertNotEquals(null, new FizzBuzz(15));
    }

    /**
     * 別の型との比較で equals が false を返すことを確認する。
     */
    @Test
    @DisplayName("別の型が渡された場合equalsがfalseを返す")
    void 別の型が渡された場合equalsがfalseを返す() {
        assertNotEquals("15", new FizzBuzz(15));
    }

    /**
     * 同じ数値の FizzBuzz は同じハッシュ値を返すことを確認する。
     */
    @Test
    @DisplayName("同じ数値の場合hashCodeが一致する")
    void 同じ数値の場合hashCodeが一致する() {
        assertEquals(new FizzBuzz(15).hashCode(), new FizzBuzz(15).hashCode());
    }
}

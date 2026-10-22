package com.microsoft.japan.fizzbuzz.domain;

/**
 * FizzBuzz の計算範囲が不正な場合に発生する例外。
 */
public final class InvalidFizzBuzzRangeException extends IllegalArgumentException {

    /**
     * 不正な理由を指定して例外を生成する。
     *
     * @param message 不正な理由
     */
    public InvalidFizzBuzzRangeException(final String message) {
        super(message);
    }
}

package com.microsoft.japan.fizzbuzz.domain;

/**
 * 指定した数値の FizzBuzz が保存されていない場合に発生する例外。
 */
public final class FizzBuzzNotFoundException extends RuntimeException {

    /**
     * 保存されていない数値を指定して例外を生成する。
     *
     * @param number 保存されていなかった数値
     */
    public FizzBuzzNotFoundException(final int number) {
        super("FizzBuzz for " + number + " was not found.");
    }
}

package com.microsoft.japan.fizzbuzz.application.register;

/**
 * FizzBuzz の登録結果。
 *
 * @param number 数値
 * @param value  Fizz、Buzz、FizzBuzz、または数値の文字列
 */
public record RegisterFizzBuzzResult(int number, String value) {
}

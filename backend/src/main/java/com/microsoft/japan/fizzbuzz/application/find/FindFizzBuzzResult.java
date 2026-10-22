package com.microsoft.japan.fizzbuzz.application.find;

/**
 * 保存済みの FizzBuzz の取得結果。
 *
 * @param number 数値
 * @param value  Fizz、Buzz、FizzBuzz、または数値の文字列
 */
public record FindFizzBuzzResult(int number, String value) {
}

package com.microsoft.japan.fizzbuzz.application.find;

/**
 * 保存済みの FizzBuzz の取得を依頼するクエリ。
 *
 * @param number 取得する FizzBuzz の数値
 */
public record FindFizzBuzzQuery(int number) {
}

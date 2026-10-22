package com.microsoft.japan.fizzbuzz.adapter.in.web;

import com.microsoft.japan.fizzbuzz.application.find.FindFizzBuzzResult;
import com.microsoft.japan.fizzbuzz.application.register.RegisterFizzBuzzResult;

/**
 * FizzBuzz 1 件分のレスポンス。
 *
 * @param number 数値
 * @param value  Fizz、Buzz、FizzBuzz、または数値の文字列
 */
public record FizzBuzzResponse(int number, String value) {

    /**
     * 登録結果からレスポンスを生成する。
     *
     * @param result 登録ユースケースの結果
     * @return レスポンス
     */
    public static FizzBuzzResponse from(final RegisterFizzBuzzResult result) {
        return new FizzBuzzResponse(result.number(), result.value());
    }

    /**
     * 取得結果からレスポンスを生成する。
     *
     * @param result 取得ユースケースの結果
     * @return レスポンス
     */
    public static FizzBuzzResponse from(final FindFizzBuzzResult result) {
        return new FizzBuzzResponse(result.number(), result.value());
    }
}

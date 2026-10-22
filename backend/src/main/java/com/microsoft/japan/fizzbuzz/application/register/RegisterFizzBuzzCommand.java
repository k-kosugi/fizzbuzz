package com.microsoft.japan.fizzbuzz.application.register;

/**
 * FizzBuzz の登録を依頼するコマンド。
 *
 * @param number 登録する数値 (1 以上)
 */
public record RegisterFizzBuzzCommand(int number) {
}

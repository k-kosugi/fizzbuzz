package com.microsoft.japan.fizzbuzz.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * FizzBuzz 登録 API のリクエスト。
 *
 * @param number 登録する数値 (1 以上)
 */
public record RegisterFizzBuzzRequest(@NotNull @Min(1) Integer number) {
}

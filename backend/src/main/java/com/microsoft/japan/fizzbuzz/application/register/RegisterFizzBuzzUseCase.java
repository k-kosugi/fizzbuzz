package com.microsoft.japan.fizzbuzz.application.register;

/**
 * FizzBuzz を 1 件登録するユースケース。
 */
public interface RegisterFizzBuzzUseCase {

    /**
     * 指定した数値の FizzBuzz を保存し、結果を返す。保存に成功した場合のみ結果を返す。
     *
     * @param command 登録する数値を持つコマンド
     * @return 登録した FizzBuzz の結果
     * @throws IllegalArgumentException 数値が 0 以下の場合
     */
    RegisterFizzBuzzResult register(RegisterFizzBuzzCommand command);
}

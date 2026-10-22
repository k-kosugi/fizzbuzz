package com.microsoft.japan.fizzbuzz.application.find;

/**
 * 保存済みの FizzBuzz を 1 件取得するユースケース。
 */
public interface FindFizzBuzzUseCase {

    /**
     * 指定した数値の保存済みの FizzBuzz を返す。
     *
     * @param query 取得する数値を持つクエリ
     * @return 保存済みの FizzBuzz の結果
     * @throws com.microsoft.japan.fizzbuzz.domain.FizzBuzzNotFoundException 指定した数値が保存されていない場合
     */
    FindFizzBuzzResult find(FindFizzBuzzQuery query);
}

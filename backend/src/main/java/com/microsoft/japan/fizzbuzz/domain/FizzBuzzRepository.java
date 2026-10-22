package com.microsoft.japan.fizzbuzz.domain;

import java.util.Optional;

/**
 * FizzBuzz を保存する Repository。
 * FizzBuzz の識別子は数値なので、同じ数値の FizzBuzz は 1 件として保存する。
 */
public interface FizzBuzzRepository {

    /**
     * FizzBuzz を 1 件保存する。
     * すでに保存済みの数値は、そのまま (重複させず) にする。
     *
     * @param fizzBuzz 保存する FizzBuzz
     */
    void save(FizzBuzz fizzBuzz);

    /**
     * 数値を識別子として FizzBuzz を 1 件取得する。
     *
     * @param number 取得する FizzBuzz の数値
     * @return 保存済みの FizzBuzz。保存されていない場合は空
     */
    Optional<FizzBuzz> findByNumber(int number);
}

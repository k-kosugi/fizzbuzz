package com.microsoft.japan.fizzbuzz.domain;

import java.util.List;
import java.util.stream.IntStream;

/**
 * FizzBuzz を計算する数値の範囲を表す Value Object。
 * from 以上 to 以下の範囲を表し、範囲内の数値ごとに FizzBuzz を生成する。
 * 事前条件は次のとおり。
 * <ul>
 *   <li>from は 1 以上</li>
 *   <li>to は from 以上</li>
 *   <li>範囲内の数値の個数は MAX_SIZE 以下 (巨大な範囲による負荷を防ぐ)</li>
 * </ul>
 *
 * @param from 範囲の開始値 (この値を含む)
 * @param to   範囲の終了値 (この値を含む)
 */
public record FizzBuzzRange(int from, int to) {

    /** 範囲内に含められる数値の最大個数 */
    public static final int MAX_SIZE = 1000;

    /** 範囲の開始値として許可する最小値 */
    private static final int MIN_NUMBER = 1;

    /**
     * 事前条件を検証する。
     *
     * @throws InvalidFizzBuzzRangeException 事前条件を満たさない場合
     */
    public FizzBuzzRange {
        if (from < MIN_NUMBER) {
            throw new InvalidFizzBuzzRangeException("from must be at least " + MIN_NUMBER);
        }

        if (to < from) {
            throw new InvalidFizzBuzzRangeException("to must be greater than or equal to from");
        }

        // int の範囲を超えないよう long で個数を求める
        if ((long) to - from + 1 > MAX_SIZE) {
            throw new InvalidFizzBuzzRangeException("range size must be at most " + MAX_SIZE);
        }
    }

    /**
     * 範囲内の数値ごとに FizzBuzz を生成する。
     *
     * @return from から to までの FizzBuzz (昇順)
     */
    public List<FizzBuzz> fizzBuzzes() {
        return IntStream.rangeClosed(from, to).mapToObj(FizzBuzz::new).toList();
    }
}

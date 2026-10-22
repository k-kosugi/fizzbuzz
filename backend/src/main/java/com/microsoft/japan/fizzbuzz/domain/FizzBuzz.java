package com.microsoft.japan.fizzbuzz.domain;

/**
 * FizzBuzz のドメインモデル (Entity)。
 * 判定対象の数値を 1 つだけ持ち、その数値を識別子とする。同じ数値の FizzBuzz は同一の Entity とみなす。
 * Fizz / Buzz / FizzBuzz の判定と文字列表現は自身で行う。
 */
public final class FizzBuzz {

    /** 3 の倍数のときに表示する文字列 */
    public static final String FIZZ_STRING = "Fizz";

    /** 5 の倍数のときに表示する文字列 */
    public static final String BUZZ_STRING = "Buzz";
    
    /** 3 と 5 の両方の倍数のときに表示する文字列 */
    public static final String FIZZBUZZ_STRING = FIZZ_STRING + BUZZ_STRING;

    /** Fizz の判定に使う除数 */
    public static final int FIZZ_DEVINE = 3;
    
    /** Buzz の判定に使う除数 */
    public static final int BUZZ_DEVINE = 5;
    
    /** FizzBuzz の判定に使う除数 (3 と 5 の最小公倍数) */
    public static final int FIZZBUZZ_DEVINE = FIZZ_DEVINE * BUZZ_DEVINE;

    /** 判定対象の数値 (1 以上) */
    private final int value;

    /**
     * 指定した数値で FizzBuzz を生成する。
     *
     * @param value 判定対象の数値 (1 以上)
     * @throws IllegalArgumentException value が 0 以下の場合
     */
    public FizzBuzz(int value) {
        // 0 以下を許容しない
        if(value <= 0) {
            throw new IllegalArgumentException("value must be greater than 0");
        }

        this.value = value;
    }

    /**
     * この Entity の識別子である数値を返す。
     * 永続化など、識別子が必要な境界でのみ使用する。
     *
     * @return 判定対象の数値 (1 以上)
     */
    public final int getValue() {
        return this.value;
    }

    /**
     * 3 の倍数かどうかを判定する。
     *
     * @return 3 の倍数の場合は true、それ以外は false
     */
    public final boolean isFizz() {
        return this.value % FIZZ_DEVINE == 0;

    }

    /**
     * 5 の倍数かどうかを判定する。
     *
     * @return 5 の倍数の場合は true、それ以外は false
     */
    public final boolean isBuzz() {
        return this.value % BUZZ_DEVINE == 0;
    }

    /**
     * 3 と 5 の両方の倍数 (15 の倍数) かどうかを判定する。
     *
     * @return 15 の倍数の場合は true、それ以外は false
     */
    public final boolean isFizzBuzz() {
        return this.value % FIZZBUZZ_DEVINE == 0;
    }

    /**
     * FizzBuzz の結果を文字列で返す。
     * 3 の倍数は Fizz、5 の倍数は Buzz、両方の倍数は FizzBuzz、
     * どちらにも該当しない場合は数値そのものの文字列を返す。
     *
     * @return Fizz、Buzz、FizzBuzz、または数値の文字列
     */
    @Override 
    public final String toString() {
        var sb = new StringBuilder();

        // 3 の倍数なら Fizz を追加する
        if(this.isFizz()) {
            sb.append(FIZZ_STRING);
        }

        // 5 の倍数なら Buzz を追加する (両方の倍数なら FizzBuzz になる)
        if(this.isBuzz()) {
            sb.append(BUZZ_STRING);
        }

        // どちらにも該当しない場合は数値をそのまま返す
        if(sb.length() == 0) {
            sb.append(this.value);
        }

        return sb.toString();
    }

    /**
     * 識別子である数値が等しい場合に、同一の Entity とみなす。
     *
     * @param other 比較対象
     * @return 同じ数値の FizzBuzz の場合は true、それ以外は false
     */
    @Override
    public final boolean equals(final Object other) {
        return this == other || other instanceof FizzBuzz fizzBuzz && this.value == fizzBuzz.value;
    }

    /**
     * 識別子である数値からハッシュ値を求める。
     *
     * @return ハッシュ値
     */
    @Override
    public final int hashCode() {
        return Integer.hashCode(this.value);
    }
}

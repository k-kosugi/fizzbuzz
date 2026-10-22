package com.microsoft.japan.fizzbuzz.adapter.out.persistence;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

/**
 * FizzBuzz をメモリ上に保存する Repository の実装。
 * 数値が識別子なので、保存済みの数値は重複させない。
 * 数値に加えて、isFizz、isBuzz、isFizzBuzz、toString の結果も列として保存する。
 * アプリケーションを停止すると保存した内容は失われる。
 */
public final class InMemoryFizzBuzzRepository implements FizzBuzzRepository {

    /**
     * メモリ上の 1 行。
     *
     * @param number     数値 (識別子)
     * @param isFizz     3 の倍数かどうか
     * @param isBuzz     5 の倍数かどうか
     * @param isFizzBuzz 15 の倍数かどうか
     * @param value      toString の結果
     */
    public record Row(int number, boolean isFizz, boolean isBuzz, boolean isFizzBuzz, String value) {

        private static Row from(final FizzBuzz fizzBuzz) {
            return new Row(fizzBuzz.getValue(), fizzBuzz.isFizz(), fizzBuzz.isBuzz(), fizzBuzz.isFizzBuzz(),
                    fizzBuzz.toString());
        }
    }

    /** 数値の昇順で保持する保存先 */
    private final Map<Integer, Row> rows = new ConcurrentSkipListMap<>();

    /**
     * {@inheritDoc}
     * @throws NullPointerException fizzBuzz が null の場合
     */
    @Override
    public void save(final FizzBuzz fizzBuzz) {
        Objects.requireNonNull(fizzBuzz, "FizzBuzz must not be null");

        rows.putIfAbsent(fizzBuzz.getValue(), Row.from(fizzBuzz));
    }

    @Override
    public Optional<FizzBuzz> findByNumber(final int number) {
        return Optional.ofNullable(rows.get(number)).map(row -> new FizzBuzz(row.number()));
    }

    /**
     * @return 保存済みの行 (数値の昇順)
     */
    public List<Row> findAllRows() {
        return List.copyOf(rows.values());
    }

    /**
     * 保存済みの行をすべて削除する。
     */
    public void deleteAll() {
        rows.clear();
    }
}

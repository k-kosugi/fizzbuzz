package com.microsoft.japan.fizzbuzz.adapter.out.persistence;

import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

/**
 * PostgreSQL に FizzBuzz を保存する Repository の実装。
 * 現在は Spring の管理 Bean として登録しておらず、使用していない。
 * 数値が主キーなので、保存済みの数値は重複させない。
 * 数値に加えて、isFizz、isBuzz、isFizzBuzz、toString の結果も保存する。
 */
public final class JdbcFizzBuzzRepository implements FizzBuzzRepository {

    /** 保存済みの数値を無視して追加する SQL */
    private static final String INSERT_SQL = """
            INSERT INTO fizzbuzz (number, is_fizz, is_buzz, is_fizz_buzz, value)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (number) DO NOTHING
            """;

    /** 数値を指定して取得する SQL */
    private static final String SELECT_SQL = "SELECT number FROM fizzbuzz WHERE number = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * @param jdbcTemplate SQL の実行に使用するテンプレート
     */
    public JdbcFizzBuzzRepository(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * {@inheritDoc}
     * 保存に失敗した場合は DataAccessException が発生する。
     */
    @Override
    public void save(final FizzBuzz fizzBuzz) {
        jdbcTemplate.update(INSERT_SQL, fizzBuzz.getValue(), fizzBuzz.isFizz(), fizzBuzz.isBuzz(),
                fizzBuzz.isFizzBuzz(), fizzBuzz.toString());
    }

    @Override
    public Optional<FizzBuzz> findByNumber(final int number) {
        return jdbcTemplate.query(SELECT_SQL, (resultSet, rowNumber) -> new FizzBuzz(resultSet.getInt("number")),
                number).stream().findFirst();
    }
}

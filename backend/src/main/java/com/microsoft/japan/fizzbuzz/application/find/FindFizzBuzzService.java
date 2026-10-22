package com.microsoft.japan.fizzbuzz.application.find;

import java.util.Objects;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzzNotFoundException;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

/**
 * 保存済みの FizzBuzz を 1 件取得するアプリケーションサービス。
 */
public final class FindFizzBuzzService implements FindFizzBuzzUseCase {

    /** FizzBuzz の保存先 */
    private final FizzBuzzRepository repository;

    /**
     * @param repository FizzBuzz の保存先
     * @throws NullPointerException repository が null の場合
     */
    public FindFizzBuzzService(final FizzBuzzRepository repository) {
        this.repository = Objects.requireNonNull(repository, "FizzBuzzRepository must not be null");
    }

    @Override
    public FindFizzBuzzResult find(final FindFizzBuzzQuery query) {
        return repository.findByNumber(query.number())
                .map(fizzBuzz -> new FindFizzBuzzResult(fizzBuzz.getValue(), fizzBuzz.toString()))
                .orElseThrow(() -> new FizzBuzzNotFoundException(query.number()));
    }
}

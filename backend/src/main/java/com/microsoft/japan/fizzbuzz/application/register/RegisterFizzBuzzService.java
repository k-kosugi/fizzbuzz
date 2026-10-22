package com.microsoft.japan.fizzbuzz.application.register;

import java.util.Objects;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

/**
 * FizzBuzz を 1 件登録するアプリケーションサービス。
 */
public final class RegisterFizzBuzzService implements RegisterFizzBuzzUseCase {

    /** FizzBuzz の保存先 */
    private final FizzBuzzRepository repository;

    /**
     * @param repository FizzBuzz の保存先
     * @throws NullPointerException repository が null の場合
     */
    public RegisterFizzBuzzService(final FizzBuzzRepository repository) {
        this.repository = Objects.requireNonNull(repository, "FizzBuzzRepository must not be null");
    }

    /**
     * {@inheritDoc}
     * 保存に失敗した場合は結果を返さず、例外をそのまま呼び出し元へ伝える。
     */
    @Override
    public RegisterFizzBuzzResult register(final RegisterFizzBuzzCommand command) {
        final var fizzBuzz = new FizzBuzz(command.number());

        repository.save(fizzBuzz);

        return new RegisterFizzBuzzResult(fizzBuzz.getValue(), fizzBuzz.toString());
    }
}

package com.microsoft.japan.fizzbuzz.application.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * RegisterFizzBuzzService の単体テスト。Repository にはメモリ上のフェイクを使用する。
 */
class RegisterFizzBuzzServiceTest {

    @Test
    @DisplayName("repositoryにnullが渡された場合NullPointerExceptionが発生する")
    void repositoryにnullが渡された場合NullPointerExceptionが発生する() {
        assertThrows(NullPointerException.class, () -> new RegisterFizzBuzzService(null));
    }

    @Test
    @DisplayName("numberに3が渡された場合数値とFizzを返す")
    void numberに3が渡された場合数値とFizzを返す() {
        final var service = new RegisterFizzBuzzService(new FakeRepository());

        assertEquals(new RegisterFizzBuzzResult(3, "Fizz"), service.register(new RegisterFizzBuzzCommand(3)));
    }

    @Test
    @DisplayName("numberに3が渡された場合そのFizzBuzzを保存する")
    void numberに3が渡された場合そのFizzBuzzを保存する() {
        final var repository = new FakeRepository();

        new RegisterFizzBuzzService(repository).register(new RegisterFizzBuzzCommand(3));

        assertEquals(List.of(new FizzBuzz(3)), repository.saved);
    }

    @Test
    @DisplayName("numberに0が渡された場合IllegalArgumentExceptionが発生する")
    void numberに0が渡された場合IllegalArgumentExceptionが発生する() {
        final var service = new RegisterFizzBuzzService(new FakeRepository());

        assertThrows(IllegalArgumentException.class, () -> service.register(new RegisterFizzBuzzCommand(0)));
    }

    @Test
    @DisplayName("numberに0が渡された場合何も保存しない")
    void numberに0が渡された場合何も保存しない() {
        final var repository = new FakeRepository();
        final var service = new RegisterFizzBuzzService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.register(new RegisterFizzBuzzCommand(0)));

        assertTrue(repository.saved.isEmpty());
    }

    @Test
    @DisplayName("保存に失敗した場合その例外をそのまま伝える")
    void 保存に失敗した場合その例外をそのまま伝える() {
        final var failure = new IllegalStateException("Persistence failed");
        final FizzBuzzRepository repository = new FakeRepository() {
            @Override
            public void save(final FizzBuzz fizzBuzz) {
                throw failure;
            }
        };
        final var service = new RegisterFizzBuzzService(repository);

        final var thrown = assertThrows(IllegalStateException.class,
                () -> service.register(new RegisterFizzBuzzCommand(1)));

        assertSame(failure, thrown);
    }

    /**
     * テスト用のフェイク。保存された FizzBuzz を記録する。
     */
    private static class FakeRepository implements FizzBuzzRepository {

        private final List<FizzBuzz> saved = new ArrayList<>();

        @Override
        public void save(final FizzBuzz fizzBuzz) {
            saved.add(fizzBuzz);
        }

        @Override
        public Optional<FizzBuzz> findByNumber(final int number) {
            return Optional.empty();
        }
    }
}

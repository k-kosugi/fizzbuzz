package com.microsoft.japan.fizzbuzz.application.find;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzNotFoundException;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FindFizzBuzzService の単体テスト。Repository にはフェイクを使用する。
 */
class FindFizzBuzzServiceTest {

    /** 3 だけを保存済みとして返すフェイク */
    private static final FizzBuzzRepository REPOSITORY_WITH_THREE = new FizzBuzzRepository() {
        @Override
        public void save(final FizzBuzz fizzBuzz) {
        }

        @Override
        public Optional<FizzBuzz> findByNumber(final int number) {
            return number == 3 ? Optional.of(new FizzBuzz(3)) : Optional.empty();
        }
    };

    @Test
    @DisplayName("repositoryにnullが渡された場合NullPointerExceptionが発生する")
    void repositoryにnullが渡された場合NullPointerExceptionが発生する() {
        assertThrows(NullPointerException.class, () -> new FindFizzBuzzService(null));
    }

    @Test
    @DisplayName("保存済みのnumberに3が渡された場合数値とFizzを返す")
    void 保存済みのnumberに3が渡された場合数値とFizzを返す() {
        final var service = new FindFizzBuzzService(REPOSITORY_WITH_THREE);

        assertEquals(new FindFizzBuzzResult(3, "Fizz"), service.find(new FindFizzBuzzQuery(3)));
    }

    @Test
    @DisplayName("保存されていないnumberに4が渡された場合FizzBuzzNotFoundExceptionが発生する")
    void 保存されていないnumberに4が渡された場合FizzBuzzNotFoundExceptionが発生する() {
        final var service = new FindFizzBuzzService(REPOSITORY_WITH_THREE);

        assertThrows(FizzBuzzNotFoundException.class, () -> service.find(new FindFizzBuzzQuery(4)));
    }
}

package com.microsoft.japan.fizzbuzz.adapter.out.persistence;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.microsoft.japan.fizzbuzz.adapter.out.persistence.InMemoryFizzBuzzRepository.Row;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryFizzBuzzRepositoryTest {

    private final InMemoryFizzBuzzRepository repository = new InMemoryFizzBuzzRepository();

    @Test
    @DisplayName("3の倍数が渡された場合isFizzだけtrueの行を保存する")
    void 三の倍数が渡された場合isFizzだけtrueの行を保存する() {
        repository.save(new FizzBuzz(3));

        assertEquals(List.of(new Row(3, true, false, false, "Fizz")), repository.findAllRows());
    }

    @Test
    @DisplayName("5の倍数が渡された場合isBuzzだけtrueの行を保存する")
    void 五の倍数が渡された場合isBuzzだけtrueの行を保存する() {
        repository.save(new FizzBuzz(5));

        assertEquals(List.of(new Row(5, false, true, false, "Buzz")), repository.findAllRows());
    }

    @Test
    @DisplayName("15の倍数が渡された場合すべてtrueの行を保存する")
    void 十五の倍数が渡された場合すべてtrueの行を保存する() {
        repository.save(new FizzBuzz(15));

        assertEquals(List.of(new Row(15, true, true, true, "FizzBuzz")), repository.findAllRows());
    }

    @Test
    @DisplayName("どれの倍数でもない数が渡された場合すべてfalseで数値の文字列の行を保存する")
    void どれの倍数でもない数が渡された場合すべてfalseで数値の文字列の行を保存する() {
        repository.save(new FizzBuzz(1));

        assertEquals(List.of(new Row(1, false, false, false, "1")), repository.findAllRows());
    }

    @Test
    @DisplayName("保存済みの数値が渡された場合重複せずに1件のままにする")
    void 保存済みの数値が渡された場合重複せずに1件のままにする() {
        repository.save(new FizzBuzz(2));
        repository.save(new FizzBuzz(2));

        assertEquals(1, repository.findAllRows().size());
    }

    @Test
    @DisplayName("複数の数値が渡された場合数値の昇順で保存する")
    void 複数の数値が渡された場合数値の昇順で保存する() {
        repository.save(new FizzBuzz(3));
        repository.save(new FizzBuzz(1));

        assertEquals(List.of(1, 3), repository.findAllRows().stream().map(Row::number).toList());
    }

    @Test
    @DisplayName("nullが渡された場合NullPointerExceptionが発生する")
    void nullが渡された場合NullPointerExceptionが発生する() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }

    @Test
    @DisplayName("保存済みの数値が渡された場合そのFizzBuzzを返す")
    void 保存済みの数値が渡された場合そのFizzBuzzを返す() {
        repository.save(new FizzBuzz(3));

        assertEquals(new FizzBuzz(3), repository.findByNumber(3).orElseThrow());
    }

    @Test
    @DisplayName("保存されていない数値が渡された場合空を返す")
    void 保存されていない数値が渡された場合空を返す() {
        assertTrue(repository.findByNumber(3).isEmpty());
    }

    @Test
    @DisplayName("deleteAllが呼ばれた場合保存済みの内容をすべて削除する")
    void deleteAllが呼ばれた場合保存済みの内容をすべて削除する() {
        repository.save(new FizzBuzz(1));

        repository.deleteAll();

        assertTrue(repository.findAllRows().isEmpty());
    }
}

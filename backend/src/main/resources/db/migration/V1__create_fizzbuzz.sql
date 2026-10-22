-- FizzBuzz の Entity を保存する。識別子は数値そのもので、同じ数値は 1 行になる。
-- isFizz、isBuzz、isFizzBuzz、toString の結果も合わせて保存する。
CREATE TABLE fizzbuzz (
    number INTEGER PRIMARY KEY CHECK (number >= 1),
    is_fizz BOOLEAN NOT NULL,
    is_buzz BOOLEAN NOT NULL,
    is_fizz_buzz BOOLEAN NOT NULL,
    value TEXT NOT NULL CHECK (btrim(value) <> '')
);

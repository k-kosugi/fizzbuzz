package com.microsoft.japan.fizzbuzz.adapter.in.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.microsoft.japan.fizzbuzz.domain.FizzBuzzNotFoundException;
import com.microsoft.japan.fizzbuzz.domain.InvalidFizzBuzzRangeException;

/**
 * エラーを Problem Details 形式で返す例外ハンドラー。
 * JSON の不正や必須項目の欠落などの標準的な例外は、親クラスが Problem Details に変換する。
 */
@RestControllerAdvice
public final class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * 範囲が不正な場合、理由を含めて HTTP 400 を返す。
     *
     * @param exception 範囲が不正であることを表す例外
     * @return HTTP 400 の Problem Details
     */
    @ExceptionHandler(InvalidFizzBuzzRangeException.class)
    public ProblemDetail handleInvalidRange(final InvalidFizzBuzzRangeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    /**
     * 指定した数値の FizzBuzz が保存されていない場合、HTTP 404 を返す。
     *
     * @param exception 保存されていないことを表す例外
     * @return HTTP 404 の Problem Details
     */
    @ExceptionHandler(FizzBuzzNotFoundException.class)
    public ProblemDetail handleNotFound(final FizzBuzzNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    /**
     * 保存に失敗した場合、原因をログに記録し、内部情報を含めず HTTP 500 を返す。
     *
     * @param exception 保存時に発生したデータアクセス例外
     * @return HTTP 500 の Problem Details
     */
    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handlePersistenceFailure(final DataAccessException exception) {
        LOGGER.error("Failed to persist FizzBuzz", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "The FizzBuzz results could not be saved.");
    }
}

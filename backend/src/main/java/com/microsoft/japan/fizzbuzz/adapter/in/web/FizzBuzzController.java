package com.microsoft.japan.fizzbuzz.adapter.in.web;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.microsoft.japan.fizzbuzz.application.find.FindFizzBuzzQuery;
import com.microsoft.japan.fizzbuzz.application.find.FindFizzBuzzUseCase;
import com.microsoft.japan.fizzbuzz.application.register.RegisterFizzBuzzCommand;
import com.microsoft.japan.fizzbuzz.application.register.RegisterFizzBuzzUseCase;

import jakarta.validation.Valid;

/**
 * FizzBuzz を 1 件ずつ登録・取得する REST API のコントローラー。
 */
@RestController
@RequestMapping("/api/fizzbuzz")
public final class FizzBuzzController {

    private final RegisterFizzBuzzUseCase registerUseCase;
    private final FindFizzBuzzUseCase findUseCase;

    /**
     * @param registerUseCase FizzBuzz を 1 件登録するユースケース
     * @param findUseCase     保存済みの FizzBuzz を 1 件取得するユースケース
     */
    public FizzBuzzController(final RegisterFizzBuzzUseCase registerUseCase, final FindFizzBuzzUseCase findUseCase) {
        this.registerUseCase = registerUseCase;
        this.findUseCase = findUseCase;
    }

    /**
     * 指定した数値の FizzBuzz を保存し、結果を返す。
     *
     * @param request 登録する数値
     * @return 登録した FizzBuzz の結果 (保存に成功した場合のみ)。取得先を Location ヘッダーで返す
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FizzBuzzResponse> register(@Valid @RequestBody final RegisterFizzBuzzRequest request) {
        final var result = registerUseCase.register(new RegisterFizzBuzzCommand(request.number()));
        final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{number}")
                .buildAndExpand(result.number()).toUri();
        return ResponseEntity.created(location).body(FizzBuzzResponse.from(result));
    }

    /**
     * 保存済みの FizzBuzz を 1 件返す。
     *
     * @param number 取得する数値
     * @return 保存済みの FizzBuzz。保存されていない場合は HTTP 404
     */
    @GetMapping(path = "/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public FizzBuzzResponse find(@PathVariable final int number) {
        return FizzBuzzResponse.from(findUseCase.find(new FindFizzBuzzQuery(number)));
    }
}

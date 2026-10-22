package com.microsoft.japan.fizzbuzz.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microsoft.japan.fizzbuzz.adapter.out.persistence.InMemoryFizzBuzzRepository;
import com.microsoft.japan.fizzbuzz.application.find.FindFizzBuzzService;
import com.microsoft.japan.fizzbuzz.application.find.FindFizzBuzzUseCase;
import com.microsoft.japan.fizzbuzz.application.register.RegisterFizzBuzzService;
import com.microsoft.japan.fizzbuzz.application.register.RegisterFizzBuzzUseCase;
import com.microsoft.japan.fizzbuzz.domain.FizzBuzzRepository;

/**
 * FizzBuzz の Bean 定義。
 * ドメイン層とアプリケーション層に Spring のアノテーションを付けないため、ここで組み立てる。
 */
@Configuration(proxyBeanMethods = false)
public class FizzBuzzConfiguration {

    /**
     * @return メモリ上に保存する Repository
     */
    @Bean
    public InMemoryFizzBuzzRepository fizzBuzzRepository() {
        return new InMemoryFizzBuzzRepository();
    }

    /**
     * @param repository FizzBuzz の保存先
     * @return FizzBuzz を 1 件登録するユースケース
     */
    @Bean
    public RegisterFizzBuzzUseCase registerFizzBuzzUseCase(final FizzBuzzRepository repository) {
        return new RegisterFizzBuzzService(repository);
    }

    /**
     * @param repository FizzBuzz の保存先
     * @return 保存済みの FizzBuzz を 1 件取得するユースケース
     */
    @Bean
    public FindFizzBuzzUseCase findFizzBuzzUseCase(final FizzBuzzRepository repository) {
        return new FindFizzBuzzService(repository);
    }
}

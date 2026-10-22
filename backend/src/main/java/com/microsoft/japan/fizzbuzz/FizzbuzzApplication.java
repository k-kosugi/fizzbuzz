package com.microsoft.japan.fizzbuzz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * FizzBuzz アプリケーションのエントリーポイント。
 */
@SpringBootApplication
public class FizzbuzzApplication {

	/**
	 * アプリケーションを起動する。
	 *
	 * @param args コマンドライン引数
	 */
	public static void main(String[] args) {
		SpringApplication.run(FizzbuzzApplication.class, args);
	}

}

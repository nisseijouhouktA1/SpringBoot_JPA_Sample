package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

//eclipse再起動時にjarファイルとのリンクが切れてビルドが出来なくなるエラーが発生した。 2/2
//ローカルキャッシュにjarファイルがあるのでリンクしなおすこと
//thymeleaf
//demo以下じゃないとデフォルトだとコントローラーとしてみてもらえない！！
@SpringBootApplication
@EntityScan(basePackages = "com.example.its.domain.articles")
@ComponentScan(basePackages = {"com.example.demo", "com.example.its.domain.articles"})
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

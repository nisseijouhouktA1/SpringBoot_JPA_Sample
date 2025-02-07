package com.example.demo.config;

import static org.springframework.security.config.Customizer.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//webSecurity周りのパッケージをgradleで記述すること

//httpSecurity周りのメソッドについてまとめること。

//JavaDocサンプルに書かれてないけどSpring SecurityにはwithDefaults()がないので
//staticに導入すること
//import static org.springframework.security.config.Customizer.withDefaults;

//cssやjsファイルが除外できないか調べること。
//色々とSpring Securityのリファレンスは不親切かもしれない
//https://docs.spring.io/spring-security/reference/api/java/index.html
//一応JavaDoc
@Configuration
@EnableWebSecurity
public class FormLoginSecurityConfig {

	//secyruttFillterChainについて調べる。
	
    //カスタムしたい場合はbuild()する前に処理を挟むこと
    //セッションに保存する生存期間を設定したい場合、
	//https://docs.spring.io/spring-security/site/docs/6.4.2/api/org/springframework/security/web/session/package-summary.html
	/*
	 * HttpSession周りの資料は以下を参考
	 * 
	 * 
	 * https://spring.pleiades.io/spring-security/reference/servlet/authentication/session-management.html
	 * 
	 * (参照)
	 * 
	 * セッションの有効期限は
	 * spring.session.timeout= 20(秒指定)みたいに変更する (application.property)
	 * 
	 * ユーザー情報とかを使いたいとか
	 * ログインユーザーの情報をセッションに保存とかできないか調べる。
	 * つまりSessionに保存したログイン時の情報について他のviewやカスタムページで使用する。
	 */
	
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.requestMatchers("/**").hasRole("USER")).formLogin(withDefaults());
        
        http
        .sessionManagement(session -> session
            .invalidSessionUrl("/login")
        );
     
        //何かしら処理を挟みたい場合はここに書く。
        //http.hogehoge().piyopiyo();
        //ヘッダのチェックとか(フォームログインセキュリティコンフィグという名前が間違いになるけど)
        
		return http.build();
	}
    //UserDetailsServiceについて調べる。
    
    //テスト用の為、pass,userはそのまま
    //こちらをデータベースから取得する形に修正する。
    //ページ上でif:authの様に出来るか確認する。    
    //SecurityContextHolder.getContext().getAuthentication() 
    //上記関数でコンテキストを取得可能(つまりコントローラー側でロジックが書ける)
    
	@Bean
    UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("user")
			.password("{noop}pass")
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
		
		//セキュリティ認証としてインメモリ認証を行っている。
		//https://spring.pleiades.io/spring-security/reference/servlet/authentication/passwords/in-memory.html
	}
}

/*
 * 
 * Security FilterChainクラスと、HttpSecurityクラス、UserDetailServiceについて
 * ある程度自分の中で理解をしておく必要がある。
 * UserDetails と UserDetailsService は、Spring Security の挙動をカスタマイズする際に登場するインターフェース
 * 
 * インターフェースの実装を見てどんなのが実装されるのかざっと見ておく。
 *
 * 実装クラスは一応含まれている。
 *UserDetails // 単にユーザー情報を保存するクラス(インターフェース) 
 * 
 * 
 * 
 * https://poco-tech.com/posts/spring-security-introduction/userdetails-and-userdetailsservice/
 * (参考)
 * 
 * 
 */
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
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.requestMatchers("/**").hasRole("USER")).formLogin(withDefaults());
		return http.build();
	}
    //UserDetailsServiceについて調べる。
    
	@Bean
    UserDetailsService userDetailsService() {
		UserDetails user = User.withUsername("user")
			.password("{noop}pass")
			.roles("USER")
			.build();
		return new InMemoryUserDetailsManager(user);
	}
}

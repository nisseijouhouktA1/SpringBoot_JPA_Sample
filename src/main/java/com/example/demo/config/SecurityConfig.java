//package com.example.demo.config;
//
//import java.beans.Customizer;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//import org.springframework.security.web.SecurityFilterChain;
//
//
////webSecurity周りのパッケージをgradleで記述すること
//
////httpSecurity周りのメソッドについてまとめること。
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    	//httpSecurityはアクセス制御の根幹的なクラスでSpringでは
//    	//アホほど使い倒されてるぽいので注意すること。
//    	//Responseホゲホゲはまた別のやつだけど。
//    	http
//    	.authorizeHttpRequests((authorize) -> authorize
//				.anyRequest().authenticated()
//			)
//			.formLogin(Customizer.withDefaults());
//
//		return http.build();
//	}
//
//    @Bean
//    UserDetailsService userDetailsService() {
//		UserDetails userDetails = User.withDefaultPasswordEncoder()
//			.username("user")
//			.password("password")
//			.roles("USER")
//			.build();
//
//		return new InMemoryUserDetailsManager(userDetails);
//	}
//
//}
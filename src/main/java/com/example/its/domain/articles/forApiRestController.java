package com.example.its.domain.articles;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class forApiRestController {
@GetMapping(value = "/usrname/")
public String cuurentUserNmae(Authentication auth) {
//	Spring Securityが自動でインスタンスを注入してくれるらしいです。
	
//静的型付けなのでnullチェックを忘れないように。
	
	if(auth != null) {
return auth.getName();	
	}
	return "Error";
}
}

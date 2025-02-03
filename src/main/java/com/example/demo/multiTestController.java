package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class multiTestController {
//2つ目のコントローラーが動くかのテスト
	@GetMapping("/arsu")
	  public String showList(Model model){
		  //articleListという指定した名前でモデルが参照できる
	   // model.addAttribute("articleList", articleService.findAll());
	    return "art/arti";
	  }
}

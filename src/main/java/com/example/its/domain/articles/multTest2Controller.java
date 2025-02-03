package com.example.its.domain.articles;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class multTest2Controller {
//3つ目のコントローラーが動くかのテスト
	
	//コンストラクタがこの変数を解決してくれる。(DI)
	  private final ArticleService articleService;
	@GetMapping("/arsua")
	  public String showList(Model model){
		  //articleListという指定した名前でモデルが参照できる
	    model.addAttribute("articleList", articleService.findAllAndSortByID(2,10));
	    return "art/arti";
	  }
}
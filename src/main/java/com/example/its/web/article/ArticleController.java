package com.example.its.web.article;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.its.domain.articles.ArticleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(value = "/articlesa")
@RequiredArgsConstructor
public class ArticleController {
  // GET /issue
  
	
  //コントローラーにサービスから持ってきて実行する。
  private final ArticleService articleService;
  /**
   * 
   * @param model
   * @return 
   */
  @GetMapping
  public String showList(Model model){
	  //articleListという指定した名前でモデルが参照できる
   // model.addAttribute("articleList", articleService.findAll());
    return "art/arti";
  }
}


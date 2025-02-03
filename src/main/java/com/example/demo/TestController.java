package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.its.domain.articles.ArticleEntity;
import com.example.its.domain.articles.ArticleService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class TestController {

	/**
	 * application.yml から取得したメッセージ。
	 */
	//@Value("${application.properties}")
	public String applicationYamlMessage = "test";

	private final ArticleService articleService;

	/**
	 * トップページのレスポンスを返す。
	 *
	 * @return ページ表示情報
	 */
	@GetMapping("/")
	public ModelAndView index() {

		System.out.println("HelloworldController#index");

		// システム・プロパティから取得
		String systemPropertyMessage = System.getProperty("com.example.demo.message");

		// 表示するデータをセット
		ModelAndView mav = new ModelAndView();
		mav.addObject("systemPropertyMessage", systemPropertyMessage);
		mav.addObject("applicationYamlMessage", applicationYamlMessage);
		mav.setViewName("index"); // ビュー名。Thymeleaf テンプレートファイルを指定

		return mav;
	}

	//ここのあたりをきちんと調べる
	//モデルの受け渡し方法について
	//thymeleafの指定方法
	//この辺のページングはリファクタリングが必要
	@GetMapping("/articles/")
	public String showList(@RequestParam(required = false) String page, Model model) {
		//articleListという指定した名前でモデルが参照できる
		//model.addAttribute("articleList", articleService.hogepiyoJDBCBypeke("asada sada"));
		int pageNumber = 0;
		if (page == null) {
			//改善余地あり。(次善策)
			//意外とこの実装で正しかったっぽい
			
			//引数が足りない場合は１ページ目に全要素を表示している。
			//	model.addAttribute("articleList", articleService.findAllByID(0, Integer.MAX_VALUE));
			//毎回カウントを走らせるのは重い？
			//もしそうならsessionStorageに保存しておくこと

			//飛んだ時にURIを合わせたものに変更できるか調べること。
			//	model.addAttribute("articleList", articleService.findAllByID(0,10));
		} else {
			//ここも若干どうかなという気はする。
			pageNumber = (Integer.parseInt(page) - 1);
		}
		try {
			model.addAttribute("articleList", articleService.findAllAndSortByID(pageNumber, 10));
			model.addAttribute("pagingProps", articleService.pagingProps(pageNumber, 10));
		} catch (Exception e) {

			model.addAttribute("articleList", articleService.findAllAndSortByID(0, 10));
			//articleListをページ番号を含めたモデルpagingとして扱うか、それとも別に使うかは悩みどころかもしれない。
			model.addAttribute("pagingProps", articleService.pagingProps(0, 10));
		}

		return "art/arti";
	}
	
	@GetMapping("/login/")
	public String showLogin(Model model) {
		ArticleEntity article = new ArticleEntity();
		article.setId(1);
		article.setIine(false);
		article.setTitle("asada");
		model.addAttribute("article",article);
		return "auth/login";
	}

	//アーティクル詳細ページ用関数
	//全部乗せコントローラーになってきている
	//この辺のDIはインターフェースで明示的に宣言しないと使えないのかは分からない
	@GetMapping("/articles/{id}")
	public String showArticle(@PathVariable Long id, Model model) {
		// 動的にページを作成するロジック
		//例外処理の機構は一元化するため、ここには記述しない。
		//Optional型だけどなんかキャストされてる？
		ArticleEntity article = articleService.findById(id)
				.orElseThrow(() -> new RuntimeException("Article not found"));
		model.addAttribute("article", article);
		return "art/articleDetail"; // articleDetail.htmlビューを返す
	}

	//クエリパラメータで渡す場合は
	@GetMapping("/articles/delete/")
	public String deleteConfirm(@RequestParam Long id, Model model) {
		ArticleEntity article = articleService.findById(id)
				.orElseThrow(() -> new RuntimeException("Article not found"));
		model.addAttribute("article", article);
		return "art/deleteDetail"; // articleにGETを送信してリダイレクト
	}

	@PostMapping("/articles/delete/")
	public String deleteArticle(@RequestParam Long id, Model model) {
		articleService.deleteArticleById(id);
		return "redirect:/articles/"; // articleにGETを送信してリダイレクト
	}

	@PostMapping("/articles/update/")
	public String updateArticle(@ModelAttribute ArticleEntity postedArticle, Model model) {
		model.addAttribute("article", new ArticleEntity());
		articleService.easyValidationForSelectBox(postedArticle.getImpressions());
		articleService.updateArticle(postedArticle);
		return "redirect:/articles/"; // articleにGETを送信してリダイレクト
	}

	@GetMapping("/articles/update/{id}")
	public String showUpdatePage(@PathVariable Long id, Model model) {
		// 動的にページを作成するロジック
		//例外処理の機構は一元化するため、ここには記述しない。
		//Optional型だけどなんかキャストされてる？
		ArticleEntity article = articleService.findById(id)
				.orElseThrow(() -> new RuntimeException("Article not found"));
		model.addAttribute("article", article);
		return "art/updateDetail"; // articleDetail.htmlビューを返す
	}

	@GetMapping("/articles/formTest/")
	public String formTest(Model model) {
		//articleListという指定した名前でモデルが参照できる
		ArticleEntity articleScheme = new ArticleEntity();
		model.addAttribute("article", articleScheme);
		return "art/formTest";
	}

	//同じアクションにMappingした方が後で楽です。
	//bindingは回避したほうが予測ができないバグが少ないかも。
	//Post時にバインドされるそれぞれの属性がどの順序で書き込まれるか気になるかもですが、
	//基本的にデフォルトコンストラクタが自動で呼び出されるので、コンストラクタが呼ばれてから
	//モデルアトリビュートにエンティティが登録される…という順序になります。安心かもです。
	//ただ、モデルにバインドする処理はデータの整合性とか型安全性とかが気になる！って場合にはいいかもですが、
	//厳密にしすぎると、バリデーション周りでエラーが発生する等で苦労するかもです。

	//デフォルトコンストラクタでの指定したパラメータ―名が用いられるので、
	//リクエストパラメータ名をオリジナルなものにしている方は注意。
	//コンストラクタのパラメタにオリジナルの名前を指定したい場合は@ConstructorPropertiesを使用する

	//下記はqueryに"name"というstring型のPostパラメタをバインドしてる。
	//binding = falseとなってるけどこれは、モデルにバインドはしないって意味なので注意。

	//モデルが事前にGetMapping時にページ内でテンプレートエンジンに渡されていることが必要になる。searchedArticle
	@PostMapping("/articles/formTest/")
	public String postDataAnythingTest(@ModelAttribute ArticleEntity postedArticle, Model model) {
		//Serviceにリポジトリ層のデータ処理を全部記述するか、こっちにもってきてもおｋにするかはわかんない。
		//saveとかはあれspringの命名パターンをチートシートとして用意する。
		//バインディングを無視する場合サービスクラスでの軌道修正が必要。
		model.addAttribute("article", new ArticleEntity());
		System.out.println(postedArticle);
		articleService.saveAndFlush(postedArticle);
		//articleListという指定した名前でモデルが参照できる
		//model.addAttribute("articleList", articleService.findAll());
		return "art/formTest";
	}

	@GetMapping("/displayDataTestByForm")
	public String displayDataTest() {
		//articleListという指定した名前でモデルが参照できる
		//model.addAttribute("articleList", articleService.findAll());
		return "art/displayDataSubmittedByForm";
	}

	//URIとリクエストクエリのパラメータを使用しようとしてる。
	@PostMapping("/articles/search/")
	public String postSearchAnythingTest(@RequestParam String page, @RequestParam(defaultValue = "") String title, Model model) {
		
		//defaultValue=""としたばあいもフィルターされることに注意
		//むしろ検索ワード(title)をnullかそれ以外かで場合分けしたほうがいいかも
		
		
		//Serviceにリポジトリ層のデータ処理を全部記述するか、こっちにもってきてもおｋにするかはわかんない。
		//saveとかはあれspringの命名パターンをチートシートとして用意する。
		//バインディングを無視する場合サービスクラスでの軌道修正が必要。
		//意外とこの実装で正しかったっぽい

		//こめんとあうと　1/28
		model.addAttribute("article", new ArticleEntity());

		model.addAttribute("currentPage", page);
		model.addAttribute("query", title);
		//これはどうなの？って思うんだけど、search→ridirectでarticlesに行くみたいなの出来ないのかなって思う。
		//この辺は課題とする。
		//とりあえず書く
		
		//defaultValue=""で初期値を指定する。
		//URLマッピングについて問題がないか調べる。
		//もしかしたらsearchではなく、GetMappingで検索も含め扱ったほうがシンプルになると思う。(結果が同じなので)
		//わざわざ/articles/search/と/articles/に分ける必要性が薄い。
		
		
		int pageNumber = 0;
		if (page == null || page.isEmpty()) {
			//改善余地あり。(次善策)
			//引数が足りない場合は１ページ目に全要素を表示している。
			//	model.addAttribute("articleList", articleService.findAllByID(0, Integer.MAX_VALUE));
			//毎回カウントを走らせるのは重い？
			//もしそうならsessionStorageに保存しておくこと

			//飛んだ時にURIを合わせたものに変更できるか調べること。
			//	model.addAttribute("articleList", articleService.findAllByID(0,10));
		} else {
			//ここも若干どうかなという気はする。
			pageNumber = (Integer.parseInt(page) - 1);
		}
		
		//count用のにもに使われる初期化処理
		
		//だいぶひどくなってきた
		//この辺の初期化処理はひどいかも
		Integer listSize = articleService.findAllByTitle(pageNumber, 10, title).size();
		try {
			model.addAttribute("articleList", articleService.findAllByTitle(pageNumber, 10, title));
			model.addAttribute("pagingProps", articleService.pagingPropsWithSearchNeedle_forTemp(pageNumber, 10,listSize));
		} catch (Exception e) {

			model.addAttribute("articleList", articleService.findAllByTitle(0, 10, title));
			//articleListをページ番号を含めたモデルpagingとして扱うか、それとも別に使うかは悩みどころかもしれない。
			model.addAttribute("pagingProps", articleService.pagingPropsWithSearchNeedle_forTemp(0, 10,listSize));
		}
		//クエリ保存の方法その1
		//次善策、リファクタ対象
		//デバッグ用の１行
		//		System.out.println(title); 
		boolean flag = false;
		if (title == null || title.isEmpty()) {
			flag = !flag;
		} else {
			;
		}
		model.addAttribute("searchFlag",flag);
		model.addAttribute("searchNeedle",title);

		//リダイレクト時に上書きされて消えてる。。
		//return "redirect:/articles/?page=" + (pageNumber + 1) + "&query=" + title;

		//解決策として検索欄を常にチェックするようにして(多分それ周りでモデルとか作ったほうがいいとか出てくるかも)
		//ペジネーション時に送るようにする。次善策その3ぐらいか…

		//禁断のsearch結果フラグがあったら送って～をthymeleaf側に作成する。
		//完全に禁じ手なのでリファクタリング前提の実装になる。(突貫工事)

		return "art/arti";
	}

	/*
	 *One to Many
	 * 
	 * */

	/**
	 * エラーページを表示するテスト用メソッド。
	 */
	// @GetMapping("/exception/")
	// public void exception() {
	//   System.out.println("HelloworldController#exception");
	//  throw new RuntimeException("This is a sample exception.");
	// }
}

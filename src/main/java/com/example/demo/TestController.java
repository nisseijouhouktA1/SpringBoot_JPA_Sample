package com.example.demo;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
		//getAuthenticationで返ってくるやつがユーザー情報とか情報を保存したあれ。
		//UserDetailのインスタンス。(コンフィグクラス参照のこと)
		Authentication principal = SecurityContextHolder.getContext().getAuthentication();
		String name = "";
		if (principal != null) {
			name = principal.getName();
		}

		System.out.println("HelloworldController#index");

		// システム・プロパティから取得
		String systemPropertyMessage = System.getProperty("com.example.demo.message");

		// 表示するデータをセット
		ModelAndView mav = new ModelAndView();
		mav.addObject("systemPropertyMessage", systemPropertyMessage);
		mav.addObject("applicationYamlMessage", applicationYamlMessage);
		//""かどうか調べるロジックをフロント側に記載する。
		//ログアウトページを作る。
		mav.addObject("userName", name);
		mav.setViewName("index"); // ビュー名。Thymeleaf テンプレートファイルを指定

		return mav;
	}

	//ページング関連の箇所
	//ここのあたりをきちんと調べる
	//モデルの受け渡し方法について
	//thymeleafの指定方法
	//この辺のページングはリファクタリングが必要
	/*
	 * Sessionを使う場合はHttpSessionを使用する。
	 * 
	 * */
	@GetMapping("/articles/")
	public String showList(@RequestParam(required = false, defaultValue = "1") String page, HttpSession session,
			Model model) {
		//articleListという指定した名前でモデルが参照できる
		//model.addAttribute("articleList", articleService.hogepiyoJDBCBypeke("asada sada"));
		int pageNumber = 0;
		System.out.println("*************************************** " + page);
		try {
			//キャスト時にエラーが発生したときのリダイレクトも兼ねてる。
			/**
			 *フォーマット例外：(URIを手作業で変更したというケース指定)
			 *をキャッチしたときにURIを明示的に指定する。
			 * 正しいフォーマットでない場合は、直前のURIに遷移する。
			 * ページ数以上の指定も無効にする。(長谷川さん指摘)
			 * 
			 *URIをチェックして込み入ったアクセス制御を行う場合、URLEditorを使ってリクエストをバリデーションするといいかもしれない
			 *直前のページとなると状態を保持している必要がある。
			 **/

			//配列の引数的なアレで-1してるけど本当はサービス側で対応するべき部分。
			pageNumber = (Integer.parseInt(page) - 1);
			System.out.println("*************************************** " + pageNumber);
			Map<String, Integer> pagengProps = articleService.pagingProps(pageNumber, 10);
			//nullの場合があるのでオブジェクト型で
			Integer totalPage = pagengProps.get("totalPage");
			if (totalPage == null || totalPage < pageNumber) {
				//ページがでかすぎたとかの場合で例外投げをする。
				throw new IllegalArgumentException("上記のページには移動出来ません。");
			}
			//例外発生時点でキャッチ節に飛ぶので例外が発生しないときだけ保存される。
			session.setAttribute("previousPageNumber", pageNumber + 1);
			model.addAttribute("articleList", articleService.findAllAndSortByID(pageNumber, 10));
			model.addAttribute("pagingProps", pagengProps);
		} catch (Exception e) {

			System.out.println(e.getMessage());
			String previousPageNumber = session.getAttribute("previousPageNumber").toString();
			if (previousPageNumber == null || previousPageNumber.isEmpty() || !previousPageNumber.matches("\\d+")) {
				//初期化
				previousPageNumber = "1";
			}
			//例外発生時にはリダイレクトを行う。
			//一番最新の正しく遷移できたページにリダイレクトする。
			return "redirect:/articles/?page=" + previousPageNumber;
		}
		return "art/arti";
	}

	/* *
	 * search用のマッピング
	 * 他にもsortや逆順での表示とか条件を保持してページングしたい場合はPostにマッピングする。
	 * 
	 * 
	 * 検索ボタンを押してきたパターンとページングで来た場合を区別する必要がある。
	 * 
	 * 現状は検索なし→GetMapping("/articles/")
	 * 
	 * 検索あり→PostMapping("/articles/")となっている。
	 * 
	 * この辺のマッピングは整理の余地があると思う。
	 *
	 * */
	@PostMapping("/articles/")
	public String searchList(@RequestParam(required = false, defaultValue = "1") String page,
			@RequestParam(defaultValue = "") String title,
			@RequestParam(required = false, defaultValue = "true") String firstSearch, HttpSession session,
			Model model) {

		int pageNumber = 0;

		System.out.println("*************************************** " + page);
		try {

			//配列の引数的なアレで-1してるけど本当はサービス側で吸収するべき。
			pageNumber = (Integer.parseInt(page) - 1);
			System.out.println("*************************************** " + pageNumber);
			Map<String, Integer> pagengProps = articleService.pagingProps(pageNumber, 10);
			//nullの場合があるのでオブジェクト型で
			//あーもう酷いコード。。(この時点で全体のページ数)
			Integer totalPage = pagengProps.get("totalPage");
			if (totalPage == null || totalPage < pageNumber || title == "") {
				//ページがでかすぎたとかの場合で例外投げをする。
				throw new IllegalArgumentException("移動できないページの例外に検索語句がない場合を足した感じの例外");
			}
			List<ArticleEntity> articleList = articleService.findAllByTitle(pageNumber, 10, title);

			model.addAttribute("debug_listSize", totalPage);
			//例外発生時点でキャッチ節に飛ぶので例外が発生しないときだけ保存される。
			session.setAttribute("previousPageNumber", pageNumber + 1);
			//firstSearchが"true"の時以外は、セッションに状態を保存し次のページングでの遷移に使用する。
			if (firstSearch != "false") {
				//初期検索時には初期化する。
				//このif文は状態保持を行う変数の数が増えた分だけ拡張する。
				session.setAttribute("searchNeedle", "");
			}

			model.addAttribute("query", title);
			model.addAttribute("articleList", articleList);
			model.addAttribute("pagingProps",
					articleService.pagingPropsWithSearchNeedle_forTemp(pageNumber, 10,title));
			model.addAttribute("pa", articleList.size());

		} catch (Exception e) {

			System.out.println(e.getMessage());
			String previousPageNumber = session.getAttribute("previousPageNumber").toString();
			if (previousPageNumber == null || previousPageNumber.isEmpty() || !previousPageNumber.matches("\\d+")) {
				//初期化
				previousPageNumber = "1";
			}
			//例外発生時にはリダイレクトを行う。
			//一番最新の正しく遷移できたページにリダイレクトする。
			return "redirect:/articles/?page=" + previousPageNumber;
		}

		boolean flag = false;
		if (title == null || title.isEmpty()) {
			flag = !flag;
		} else {
			;
		}
		model.addAttribute("searchFlag", flag);
		model.addAttribute("searchNeedle", title);
		return "art/arti";
	}

	@GetMapping("/login/")
	public String showLogin(Model model) {
		ArticleEntity article = new ArticleEntity();
		article.setId(1);
		article.setIine(false);
		article.setTitle("asada");
		model.addAttribute("article", article);
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
		// 動的にページを作成するロジック\
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

	///articles/search/?page = 1,2,3....の時、/articles/?page = 1,2,3....を分けた場合について、
	///
	///メリット：
	///明確な意図: /articles/search/ にすることで、ユーザーが検索結果を閲覧していることが明確になります。
	///検索機能を前面に出すことで、ユーザーにとって検索の結果を見ているという意図が伝わりやすいです。
	///検索結果の状態を保持: 検索条件（例えば、キーワード、フィルタ、カテゴリなど）を URL に含めることで、
	///検索条件を保持したままページ遷移ができます。例えば、/articles/search/?page=2&search=keyword とすることで、
	///search クエリを使ったページングを維持できます。
	///
	///デメリット：
	///
	///実装が複雑になる(特にフロント側)
	///
	///リダイレクトを用いてsearch→クエリパラメータ付与→articlesに移動というようにできないだろうか。]
	///
	///こういう場合に、ページ用のエンティティがあると便利になる。
	///
	///
	///検索処理は/articles/に統合する。
	///
	///
	///
	@PostMapping("/articles/search/")
	public String postSearchAnythingTest(@RequestParam String page, @RequestParam(defaultValue = "") String title,
			Model model) {

		//意外とこの実装で正しかったっぽい

		model.addAttribute("article", new ArticleEntity());

		model.addAttribute("currentPage", page);
		model.addAttribute("query", title);

		int pageNumber = 0;

		//count用のにもに使われる初期化処理
		//search用の処理をarticlesに追加する。(共通処理なので、)
		//ページに使用するリスト
		Integer listSize = 0;
		try {
			pageNumber = (Integer.parseInt(page) - 1);
			listSize = articleService.findAllByTitle(pageNumber, 10, title).size();
			model.addAttribute("articleList", articleService.findAllByTitle(pageNumber, 10, title));
			model.addAttribute("pagingProps",
					articleService.pagingPropsWithSearchNeedle_forTemp(pageNumber, 10, title));
			//debug
		} catch (Exception e) {

			model.addAttribute("articleList",
					articleService.findAllByTitle(0, 10, title));
			//articleListをページ番号を含めたモデルpagingとして扱うか、それとも別に使うかは悩みどころかもしれない。
			model.addAttribute("pagingProps", articleService.pagingPropsWithSearchNeedle_forTemp(0, 10, title));
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
		model.addAttribute("searchFlag", flag);
		model.addAttribute("searchNeedle", title);
		model.addAttribute("debug_listSize", listSize);

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

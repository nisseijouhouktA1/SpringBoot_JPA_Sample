package com.example.its.domain.articles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ArticleJDBCRepository;
import com.example.demo.repository.ArticleRepository;

//どこにあっても可？フォルダ構成とか関係ない？
//謎だらけのアノテーション機能
//Serviceアノテーション（・□・；）
@Service
public class ArticleService {
	private final ArticleRepository articleRepository;
	private final ArticleJDBCRepository articlejdbcRepository;

	// コンストラクタでリポジトリを注入
	//この辺のDI周りの仕組みがどうなってんのかよくわからない。

	public ArticleService(ArticleRepository articleRepository,
			ArticleJDBCRepository articlejdbcRepository) {
		this.articleRepository = articleRepository;
		this.articlejdbcRepository = articlejdbcRepository;
	}

	public List<ArticleEntity> findAll_overWrite() {
		return List.of(
				new ArticleEntity(1, "Angular フォームボタンの無効について", "very good", false),
				new ArticleEntity(2, "Wingetで簡単！開発PCセットアップ（Windows）", "very good", false));
	}

	public void deleteArticleById(Long id) {
		Optional<ArticleEntity> article = articleRepository.findById(id);
		if (article.isPresent()) {
			articleRepository.deleteById(id); // 削除処理
		} else {
			throw new RuntimeException("Article not found with id: " + id);
		}
	}

	//フォームからポストしたデータをこのList<Model>の形式で保存する。localStorageに。。
	//オーアーマッパーを意識すること。(入れ替えが簡単にできることを意識すること。)

	//exist 
	public List<ArticleEntity> findFst() {
		return List.of(
				new ArticleEntity(2, "Wingetで簡単！開発PCセットアップ（Windows）", "very good", false));
	}

	//ページングのサンプル的に作成した関数

	//Page型をキャストして明示的にList型で返す。
	//Pageable pageable = PageRequest.of(page, size); // ページ番号とページサイズを指定
	//PageRequest.of(page, size, sort); //こっちもPageable型
	/**
	 * page,sizeを指定する。
	 * idで全ての要素を昇順にソートして、それからスライスしたページを取得する。
	 * Page型ではなくList型で返している。
	 * @param page
	 * @param size
	 * @return
	 */
	public List<ArticleEntity> findAllAndSortByID(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		return articleRepository.findAll(pageable).getContent();
	}

	//メソッドチェーンのように使えたほうがいいため大きな修正が必要かもしれない
	//fillterByTitle ?
	//findAllで手に入れてsearchでフィルターしてという風に使う場合はやはり"検索結果の確認"がネックになるため、
	//切り離して考えることがむつかしい。
	//密結合なメソッドになるかも
	//これでいいのかあんまりわからない。

	//ストリームで十分な気がするけど確かではない。
	public List<ArticleEntity> findAllByTitle(int page, int size, String title) {
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
		if (title == null || title.isEmpty()) {
			return articleRepository.findAll(pageable).getContent();
		}
		//これだと7/10とか出てくる気がする。
		//pageableで区切った時点であれなので別々にする必要があると思う。
		//だめ
		//   return articleRepository.findAll(pageable).getContent().stream().filter(article -> article.getTitle().contains(title)).toList();
		//こんな感じで作ったほうがベターなのかも？
		//return articleRepository.findAll(pageable).getContent();
			     return articleRepository.findAllByTitleContainingValues(title, pageable).getContent();
	}

	public List<ArticleEntity> getArticleById(long id) {
		//  Optional<ArticleEntity> article = articleRepository.findById((long)2);
		//nullエラーに対応するための処理を追加する。
		//id以外もインターフェースに宣言すれば使える？
		//where句、
		System.out.println("Retrieved article: " + articleRepository.findByTitle("asada sada"));
		return List.of(articleRepository.findByTitle("asada sada"));
		//.map(List::of)  // Optionalが存在すればリストにラップ
		//.orElseGet(Collections::emptyList); // Optionalが空なら空リストを返す
	}

	public List<ArticleEntity> hogepiyoJDBCBypeke(String title) {

		return List.of(articlejdbcRepository.findByPekes(title));
	}

	public void saveAndFlush(ArticleEntity postedArticle) {
		//冗長だけどメソッドの中に作る。
		//フィールドにオブジェクトの生成を作って共有は良くないかも。
		articleRepository.saveAndFlush(postedArticle);
	}

	public void updateArticle(ArticleEntity postedArticle) {

		ArticleEntity article = articleRepository.findById(postedArticle.getId())
				.orElseThrow(() -> new RuntimeException("Article not found"));
		article.setTitle(postedArticle.getTitle());
		article.setImpressions(postedArticle.getImpressions());
		article.setIine(postedArticle.isIine());
		articleRepository.saveAndFlush(article);
	}

	public Optional<ArticleEntity> findById(long id) {
		return articleRepository.findById(id);
	}

	public Map<String, Integer> pagingProps(Integer currentPage, Integer pageSize) {
		Map<String, Integer> pageInfo = new HashMap<>();
		//毎読み込みこれだと怒られる可能性もある。
		Integer totalPage = articleRepository.findAll().size() / pageSize;
		pageInfo.put("totalPage", totalPage);
		//hashMapってこういう使い方してよかったっけ？
		pageInfo.put("currentPage", currentPage);
		return pageInfo;
	}
	
	//早急にリファクタリングが必要な実装
	//listが存在する検索後にしか使用してはいけない関数
	public Map<String, Integer> pagingPropsWithSearchNeedle_forTemp(Integer currentPage, Integer pageSize,Integer listSize) {
		Map<String, Integer> pageInfo = new HashMap<>();
		//毎読み込みこれだと怒られる可能性もある。
		Integer totalPage = listSize / pageSize;
		pageInfo.put("totalPage", totalPage);
		//hashMapってこういう使い方してよかったっけ？
		pageInfo.put("currentPage", currentPage);
		return pageInfo;
	}

	public void easyValidationForSelectBox(String checkStr) {
		//固定 若干良くないかも(臨時の実装)
		List<String> allowedList = List.of("very good", "good", "irrelevant", "bad", "very bad");
		if (!allowedList.contains(checkStr)) {
			throw new RuntimeException("this value you posted here is not expected (- _ -)/ ");
		}
	}

}

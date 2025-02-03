package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.example.its.domain.articles.ArticleEntity;
public interface ArticleRepository extends PagingAndSortingRepository<ArticleEntity, Long> {
//public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
	ArticleEntity findByTitle(String title); // メソッド名だけでクエリが自動生成
	
	//List<ArticleEntity> findAllByTitle(String title); // メソッド名だけでクエリが自動生成
	
	ArticleEntity saveAndFlush(ArticleEntity article);
	//saveメソッドは標準メソッドらしく追加は必要ないらしい。
	//でも予測変換には出てこない(^^)/
	List<ArticleEntity> findAll();
	
	Optional<ArticleEntity> findById(long Id);
	
	void deleteById(long id);
	
	//たぶんjpa語を学ぶ必要がある。
	//ちなみに下のはTitleを部分検索して結果を持ってくるというjpa語
	//動かすだけならgetContentにMap関数でもいい気がするんだけどキャッシュとかとってるのかは不明
	//どっちが早いかもわからない。
	//@Queryがいいのかも
	 // Page<ArticleEntity> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
	//なんかわからんなとなったらJPQLを使用する。
	@Query("SELECT a FROM ArticleEntity a WHERE a.title LIKE %:title%")
	Page<ArticleEntity> findAllByTitleContainingValues(String title, Pageable pageable);
}



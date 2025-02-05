package com.example.its.domain.articles;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;

//@AllArgsConstructorは
//全引数のコンストラクタ対応なので、(つまり、ArticleEntity(id,title,impressions,iine)を自動生成する。)
//Idだけ抜かしたいみたいな場合は明示的にコンストラクタを記述したほうがいい
//windowsだと、テーブル名は大文字、小文字が区別されないことに注意！！
//linux環境だとなぜか動かないクソコードとかになりがちかもしれないです。
@AllArgsConstructor
@Data
@Entity
@Table(name = "article")
public class ArticleEntity {
	//デフォルト値を設定すること
	//モデルで厳密に使いたくない場合(エラーがめんどくさいので)
	//明示的に意味のありそうなデフォルト値をセットしていく
	//id INT AUTO_INCREMENTにしてなければ無意味(下の@GeneratedValueの補足)
	//無意味なことをしてた場合はALTER TABLE article MODIFY id INT AUTO_INCREMENTを実行する
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;
		private String title;
		private String impressions;
		//空要素がモデルにある場合は？
		private boolean iine;
	//	@OneToMany(mappedBy="article")
	//	private Set<iineEntity> weene;
		 
		public ArticleEntity() {
	        // デフォルトコンストラクタ
			this.title = "No title";
			this.impressions = "No impressions";
			this.iine = false;
			
	    }

}

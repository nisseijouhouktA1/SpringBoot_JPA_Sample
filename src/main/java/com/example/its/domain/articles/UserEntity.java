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
//なんかデータDBに同期してる
@AllArgsConstructor
@Data
@Entity
@Table(name = "users")
public class UserEntity {
	//デフォルト値を設定すること
	//モデルで厳密に使いたくない場合(エラーがめんどくさいので)
	//明示的に意味のありそうなデフォルト値をセットしていく
	//id INT AUTO_INCREMENTにしてなければ無意味(下の@GeneratedValueの補足)
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;
		private String name;
		private String password;
		private String email;
		//空要素がモデルにある場合は？
	//	@OneToMany(mappedBy="article")
	//	private Set<iineEntity> weene;
		 
		public UserEntity() {
	        // デフォルトコンストラクタ
			this.name = "No Name";
			this.password = "qwerty";
			this.password = "email@.com";
			
	    }

}

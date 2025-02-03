package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.its.domain.articles.ArticleEntity;

//@Query使用
public interface ArticleJDBCRepository extends JpaRepository<ArticleEntity, Long> {
    @Query("select a from ArticleEntity a where a.title = ?1")
    ArticleEntity findByPekes(String title);
    
    //jpaRepositoryでsaveするには？
    //https://spring.pleiades.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html
}

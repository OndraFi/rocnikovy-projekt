package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}

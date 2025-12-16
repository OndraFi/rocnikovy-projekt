package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByCategories_IdIn(List<Long> categoryIds, Pageable pageable);
}

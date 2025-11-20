package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Article;
import cz.upce.fei.redsys.domain.ArticleVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, Long> {

    Optional<ArticleVersion> findTopByArticleOrderByVersionNumberDesc(Article article);

    Optional<ArticleVersion> findByArticleAndVersionNumber(Article article, Integer versionNumber);

    Page<ArticleVersion> findAllByArticleOrderByVersionNumberDesc(Article article, Pageable pageable);
}

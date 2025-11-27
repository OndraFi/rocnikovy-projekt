package cz.upce.fei.redsys.components;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import cz.upce.fei.redsys.repository.CategoryRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Data init running...");
        String hashedPassword = passwordEncoder.encode("heslo123");
        User user = User.builder()
                .username("admin")
                .fullName("admin user")
                .email("admin@gmail.com")
                .password(hashedPassword)
                .role(UserRole.ADMIN)
                .build();
        if(userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating user {}", user.getEmail());
            userRepository.save(user);
        }

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        log.info("First user {}", users.getFirst());

        Category c1 = new Category();
        c1.setName("Sport");
        c1.setDescription("Sport related items");
        if(!categoryRepository.existsByName(c1.getName())) {
            log.info("Creating category {}", c1.getName());
            categoryRepository.save(c1);
        }

        Category c2 = new Category();
        c2.setName("Ekonomika");
        c2.setDescription("Ekonomika related items");
        if(!categoryRepository.existsByName(c2.getName())) {
            log.info("Creating category {}", c2.getName());
            categoryRepository.save(c2);
        }
        Set<Category> categories = new HashSet<>();
        categories.add(c1);
        categories.add(c2);

        Article article1 = new Article();
        article1.setTitle("Football News");
        article1.setArticleState(ArticleState.PUBLISHED);
        article1.setCategories(categories);
        article1.setAuthor(user);
        article1.setEditor(null);
        article1.setPublishedAt(Instant.now());
        if(!articleRepository.existsById(1L)) { // pokud není žádný článek, vytvoříme jeden
            log.info("Creating articel {}", article1.getTitle());
            articleRepository.save(article1);
        }


        if(!articleRepository.existsById(2L) || articleRepository.existsById(6L)) { // pokud není žádný článek, vytvoříme jeden
            for(int i=0; i<5; i++) {
                Article article = new Article();
                article.setTitle("Ekonomy News");
                article.setArticleState(ArticleState.PUBLISHED);
                article.setCategories(categories);
                article.setAuthor(user);
                article.setEditor(null);
                article.setPublishedAt(Instant.now());
                log.info("Creating articel {}", article.getTitle());
                article.setTitle("Ekonomy News " + (i+1));
                articleRepository.save(article);
            }
        }
    }

}

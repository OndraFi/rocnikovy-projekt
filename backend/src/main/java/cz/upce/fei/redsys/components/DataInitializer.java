package cz.upce.fei.redsys.components;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import cz.upce.fei.redsys.repository.ArticleVersionRepository;
import cz.upce.fei.redsys.repository.CategoryRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final ArticleVersionRepository articleVersionRepository;

    @Override
    public void run(String... args) {
        log.info("Data init running...");
        String hashedPassword = passwordEncoder.encode("heslo123");
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("admin")
                        .fullName("admin user")
                        .email("admin@gmail.com")
                        .password(hashedPassword)
                        .role(UserRole.ADMIN)
                        .build()));

        User editorUser = userRepository.findByUsername("editor")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("editor")
                        .fullName("editor user")
                        .email("editor@gmail.com")
                        .password(hashedPassword)
                        .role(UserRole.EDITOR)
                        .build()));

        User chiefUser = userRepository.findByUsername("chief")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("chief")
                        .fullName("chief user")
                        .email("chief@gmail.com")
                        .password(hashedPassword)
                        .role(UserRole.CHIEF_EDITOR)
                        .build()));

        Category c1 = categoryRepository.findByName("Sport")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .name("Sport")
                        .description("Sport related items")
                        .build()));

        Category c2 = categoryRepository.findByName("Ekonomika")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                            .name("Ekonomika")
                            .description("Ekonomika related items")
                            .build()));

        Set<Category> categoriesSport = new HashSet<>();
        categoriesSport.add(c1);

        Set<Category> economyCategories = new HashSet<>();
        economyCategories.add(c2);

        if (!articleRepository.existsById(1L)) { // pokud není žádný článek, vytvoříme jeden
            Article article1 = new Article();
            article1.setTitle("Football News");
            article1.setArticleState(ArticleState.PUBLISHED);
            article1.setCategories(categoriesSport);
            article1.setAuthor(chiefUser);
            article1.setEditor(editorUser);
            article1.setPublishedAt(Instant.now());
            log.info("Creating article {}", article1.getTitle());

            article1 = articleRepository.save(article1);

            String content = """
                    <h1>Football News</h1>
                    <p>This is the content of the football news article.</p>
                    
                    <h2>Latest Updates</h2>
                    
                    <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas gravida, elit quis pretium eleifend, urna ipsum tincidunt mi, sed viverra risus turpis non odio. Vestibulum feugiat, ipsum sed consectetur luctus, lacus mauris fermentum leo, sed ullamcorper enim tellus ac augue. Phasellus faucibus massa et lorem consequat feugiat.</p>
                    
                    <p>Donec id orci pretium, suscipit leo nec, fermentum risus. Sed elit nisi, faucibus at condimentum quis, interdum et lorem. Nullam sit amet lorem consequat, ultricies nisl a, tristique nibh. Morbi volutpat quam nec ante sollicitudin, vel pellentesque elit porttitor. Vestibulum sit amet commodo nulla.</p>
                    
                    <p>Integer placerat, lacus id fermentum ultricies, leo massa interdum tortor, et dictum magna felis non massa. Suspendisse bibendum enim a sem laoreet, vel ultricies arcu varius. Sed a ligula non neque tristique aliquam. Quisque vitae dolor vel lectus volutpat viverra et nec nunc.</p>
                    
                    <p>Curabitur id faucibus ex, nec placerat nisl. Duis sollicitudin velit sed orci varius, non pulvinar nunc finibus. Aliquam dapibus eros ac feugiat fermentum. Mauris aliquam est nec auctor euismod. Donec convallis erat ut magna aliquam, a venenatis justo luctus.</p>
                    
                    <p>Nam vel semper lectus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi bibendum efficitur risus nec facilisis. Praesent hendrerit libero nec nisi ultricies, ut faucibus ipsum viverra. Etiam convallis arcu vel ligula varius semper.</p>
                    """;
            ArticleVersion version = new ArticleVersion();
            version.setArticle(article1);
            version.setContent(content);
            version.setVersionNumber(1);
            version.setCreatedBy(chiefUser);
            articleVersionRepository.save(version);
        }


        if (articleRepository.count() < 5) { // pokud není žádný článek, vytvoříme jeden
            for (int i = 0; i < 5; i++) {
                Article article = new Article();
                article.setTitle("Ekonomy News");
                article.setArticleState(ArticleState.PUBLISHED);
                article.setCategories(economyCategories);
                article.setAuthor(chiefUser);
                article.setEditor(editorUser);
                article.setPublishedAt(Instant.now());
                log.info("Creating article {}", article.getTitle());
                article.setTitle("Ekonomy News " + (i + 1));
                article = articleRepository.save(article);

                String content = """
                        <h1>Ekonomi News """ + (i + 1) + """
                        </h1>
                        <p>This is the content of the football news article.</p>
                        
                        <h2>Latest Updates</h2>
                        
                        <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas gravida, elit quis pretium eleifend, urna ipsum tincidunt mi, sed viverra risus turpis non odio. Vestibulum feugiat, ipsum sed consectetur luctus, lacus mauris fermentum leo, sed ullamcorper enim tellus ac augue. Phasellus faucibus massa et lorem consequat feugiat.</p>
                        
                        <p>Donec id orci pretium, suscipit leo nec, fermentum risus. Sed elit nisi, faucibus at condimentum quis, interdum et lorem. Nullam sit amet lorem consequat, ultricies nisl a, tristique nibh. Morbi volutpat quam nec ante sollicitudin, vel pellentesque elit porttitor. Vestibulum sit amet commodo nulla.</p>
                        
                        <p>Integer placerat, lacus id fermentum ultricies, leo massa interdum tortor, et dictum magna felis non massa. Suspendisse bibendum enim a sem laoreet, vel ultricies arcu varius. Sed a ligula non neque tristique aliquam. Quisque vitae dolor vel lectus volutpat viverra et nec nunc.</p>
                        
                        <p>Curabitur id faucibus ex, nec placerat nisl. Duis sollicitudin velit sed orci varius, non pulvinar nunc finibus. Aliquam dapibus eros ac feugiat fermentum. Mauris aliquam est nec auctor euismod. Donec convallis erat ut magna aliquam, a venenatis justo luctus.</p>
                        
                        <p>Nam vel semper lectus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Morbi bibendum efficitur risus nec facilisis. Praesent hendrerit libero nec nisi ultricies, ut faucibus ipsum viverra. Etiam convallis arcu vel ligula varius semper.</p>
                        """;
                ArticleVersion version = new ArticleVersion();
                version.setArticle(article);
                version.setContent(content);
                version.setVersionNumber(1);
                version.setCreatedBy(chiefUser);

                articleVersionRepository.save(version);
            }
        }
    }

}

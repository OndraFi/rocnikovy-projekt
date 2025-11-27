package cz.upce.fei.redsys.components;

import cz.upce.fei.redsys.domain.*;
import cz.upce.fei.redsys.repository.ArticleRepository;
import cz.upce.fei.redsys.repository.ArticleVersionRepository;
import cz.upce.fei.redsys.repository.CategoryRepository;
import cz.upce.fei.redsys.repository.UserRepository;
import cz.upce.fei.redsys.service.ArticleVersionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
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
    private final ArticleVersionRepository articleVersionRepository;
    private final Environment env;


    @Override
    public void run(String... args) throws Exception {
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");
        if (!isDev) {
            return;
        }
        log.info("Data init running...");
        String hashedPassword = passwordEncoder.encode("heslo123");
        User user = User.builder()
                .username("admin")
                .fullName("admin user")
                .email("admin@gmail.com")
                .password(hashedPassword)
                .role(UserRole.ADMIN)
                .build();
        if (userRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating user {}", user.getEmail());
            userRepository.save(user);
        }

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());
        log.info("First user {}", users.getFirst());

        Category c1 = new Category();
        c1.setName("Sport");
        c1.setDescription("Sport related items");
        if (!categoryRepository.existsByName(c1.getName())) {
            log.info("Creating category {}", c1.getName());
            categoryRepository.save(c1);
        }

        Category c2 = new Category();
        c2.setName("Ekonomika");
        c2.setDescription("Ekonomika related items");
        if (!categoryRepository.existsByName(c2.getName())) {
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
        if (!articleRepository.existsById(1L)) { // pokud není žádný článek, vytvoříme jeden
            log.info("Creating articel {}", article1.getTitle());
            articleRepository.save(article1);
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
            version.setCreatedBy(user);
            articleVersionRepository.save(version);
        }


        if (!articleRepository.existsById(2L) || articleRepository.existsById(6L)) { // pokud není žádný článek, vytvoříme jeden
            for (int i = 0; i < 5; i++) {
                Article article = new Article();
                article.setTitle("Ekonomy News");
                article.setArticleState(ArticleState.PUBLISHED);
                article.setCategories(categories);
                article.setAuthor(user);
                article.setEditor(null);
                article.setPublishedAt(Instant.now());
                log.info("Creating articel {}", article.getTitle());
                article.setTitle("Ekonomy News " + (i + 1));
                articleRepository.save(article);

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
                version.setCreatedBy(user);

                articleVersionRepository.save(version);
            }
        }
    }

}

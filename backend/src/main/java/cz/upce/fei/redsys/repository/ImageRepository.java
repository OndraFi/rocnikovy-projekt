package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findImageByFilename(String filename);

    boolean existsByFilename(String fileName);

    void deleteByFilename(String fileName);
}
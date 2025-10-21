package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByOwner(User owner, Pageable pageable);
}
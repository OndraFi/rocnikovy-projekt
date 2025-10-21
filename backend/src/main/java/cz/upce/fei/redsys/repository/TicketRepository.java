package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Project;
import cz.upce.fei.redsys.domain.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findAllByProject(Project project, Pageable pageable);
    @Query("SELECT MAX(t.projectTicketNumber) FROM Ticket t WHERE t.project = :project")
    Optional<Long> findMaxProjectTicketNumber(@Param("project") Project project);
    Optional<Ticket> findByProjectAndProjectTicketNumber(Project project, Long projectTicketNumber);
}
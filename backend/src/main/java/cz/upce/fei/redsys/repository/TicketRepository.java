package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findByAssignee(User assignee, Pageable pageable);

    Page<Ticket> findByAuthor(User author, Pageable pageable);

    Page<Ticket> findByAssigneeOrAuthor(User assignee, User author, Pageable pageable);
}
package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
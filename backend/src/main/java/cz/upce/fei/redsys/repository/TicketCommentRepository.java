package cz.upce.fei.redsys.repository;

import cz.upce.fei.redsys.domain.Ticket;
import cz.upce.fei.redsys.domain.TicketComment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT COALESCE(MAX(c.ticketCommentNumber), 0) FROM TicketComment c WHERE c.ticket = :ticket")
    Integer findMaxTicketCommentNumberByTicket(@Param("ticket") Ticket ticket);

    Page<TicketComment> findAllByTicketOrderByTicketCommentNumberDesc(Ticket ticket, Pageable pageable);
    Optional<TicketComment> findByTicketAndTicketCommentNumber(Ticket ticket, Integer ticketCommentNumber);
}

package com.Kapture.Talk2Ticket.repository;

import com.kapturecrm.ticket.objects.Ticket;

import java.util.Optional;

public interface TicketRepository {

    boolean save(Ticket ticket);

    Ticket findById(Integer id);
}

package com.Kapture.Talk2Ticket.repository;

import com.Kapture.Talk2Ticket.model.TicketModal;
import com.kapturecrm.ticket.objects.Ticket;

import java.util.Optional;

public interface TicketRepository {

    boolean save(TicketModal ticket);

    Ticket findById(Integer id);
}

package com.Kapture.Talk2Ticket.service;

import com.Kapture.Talk2Ticket.repository.TicketRepository;
import com.kapturecrm.ticket.objects.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public void createTicket(String description, int priority) {
        Ticket ticket = new Ticket();
        ticket.setDetail(description);
        ticket.setPriority(priority);
        ticket.setStatus('O');
        ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }
}

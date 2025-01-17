package com.Kapture.Talk2Ticket.service;

import com.Kapture.Talk2Ticket.model.TicketModal;
import com.Kapture.Talk2Ticket.repository.TicketRepository;
import com.kapturecrm.ticket.objects.Ticket;
import facebook4j.internal.org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class TicketService {
    @Autowired
    private TicketRepository ticketRepository;

    public void createTicket(JSONObject jsonObject) {
        TicketModal ticket = new TicketModal();
        ticket.setCmId(jsonObject.optInt("cm_id"));
        ticket.setDescription(jsonObject.optString("description"));
        ticket.setPriority(jsonObject.optString("priority"));
        ticket.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        ticket.setTicketId(jsonObject.optString("ticket_id"));
        ticket.setStatus(jsonObject.optString("status"));
        ticketRepository.save(ticket);
    }

    public Ticket getTicketById(Integer id) {
        return ticketRepository.findById(id);
    }
}

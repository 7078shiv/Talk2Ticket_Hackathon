package com.Kapture.Talk2Ticket.dao;

import com.Kapture.Talk2Ticket.model.TicketModal;
import com.Kapture.Talk2Ticket.repository.TicketRepository;
import com.kapturecrm.ticket.objects.Ticket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TicketDao implements TicketRepository {

    private static Logger logger = LoggerFactory.getLogger(TicketDao.class);

    @Autowired
    SessionFactory sessionFactory;

    @Override
    public boolean save(TicketModal model) {
        boolean isClose = false;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            isClose = true;
            Transaction tx = session.beginTransaction();

            // Check if the Ticket already exists
            if (model.getId() != 0 && session.get(Ticket.class, model.getId()) != null) {
                // Update existing entity
                session.merge(model);
            } else {
                // Save new entity
                session.persist(model);
            }

            tx.commit();
            return true;

        } catch (Exception e) {
            logger.debug("Error in save() method!!!", e);
            if (session != null) {
                session.getTransaction().rollback(); // Rollback in case of error
            }
        } finally {
            if (isClose && session != null && session.isOpen()) {
                session.close();
            }
        }
        return false;
    }


    @Override
    public Ticket findById(Integer id) {
        return null;
    }
}

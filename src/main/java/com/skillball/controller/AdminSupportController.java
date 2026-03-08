package com.skillball.controller;

import com.skillball.entity.Ticket;
import com.skillball.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import javax.servlet.http.HttpServletRequest;

@Controller
public class AdminSupportController {
    @Autowired
    private TicketService ticketService;

    @GetMapping("/admin/adminSupport")
    public String showAdminSupport(Model model) {
        model.addAttribute("ticketList", ticketService.listRelevantTickets());
        return "adminSupport";
    }

    @PostMapping("/admin/sendAnswer")
    public String sendAnswer(HttpServletRequest req, Model model) {
        String id = req.getParameter("button");
        String answer = req.getParameter(id);
        int ticketId = Integer.parseInt(id);
        if (answer.length() < 3) {
            model.addAttribute("ticketList", ticketService.listRelevantTickets());
            model.addAttribute("failed", true);
            return "adminSupport";
        }
        Ticket ticket = ticketService.getTicketByTicketId(ticketId);
        ticket.setAnswer(answer);
        ticketService.saveTicket(ticket);
        model.addAttribute("ticketList", ticketService.listRelevantTickets());
        model.addAttribute("successful", true);
        return "adminSupport";
    }

    @PostMapping("/admin/deleteTicket")
    public String deleteTicket(HttpServletRequest req, Model model) {
        String id = req.getParameter("button");
        int ticketId = Integer.parseInt(id);
        Ticket ticket = ticketService.getTicketByTicketId(ticketId);
        if (ticket.isDeletedByUser()) {
            ticketService.deleteTicket(ticket);
        } else {
            ticket.setDeletedByAdmin(true);
            ticketService.saveTicket(ticket);
        }
        model.addAttribute("ticketList", ticketService.listRelevantTickets());
        return "adminSupport";
    }
}
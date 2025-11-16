package com.ramble.endpoint;

import com.embabel.agent.domain.io.UserInput;
import com.ramble.agents.TicketAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    Logger logger = LoggerFactory.getLogger(Controller.class);

    private final TicketAgent ticketAgent;

    @Autowired
    public Controller(TicketAgent ticketAgent) {
        this.ticketAgent = ticketAgent;
    }

    ;

    @GetMapping("/ramble")
    public String ramble(@RequestBody(required = false) String body) {
        logger.info("Received get body \"{}\"", body);
        return "Hello, world!";
    }

    @CrossOrigin
    @PostMapping(value = "/ramble", consumes = "text/plain", produces = "application/json")
    public Map<String, String> processText(@RequestBody String body) {
        logger.trace("Received ramble text \"{}\"", body);

        var ticket = ticketAgent.createTicket(new UserInput(body));
        Boolean creationSucceeded = ticketAgent.postTicket(ticket);

        // Trim and create a short preview
        String preview = ticket.body().length() > 50 ? ticket.body().substring(0, 50) + "..." : ticket.body();
        return Map.of(
            "status", creationSucceeded.toString(),
            "length", String.valueOf(body.length()),
            "title", ticket.title(),
            "preview", preview
        );
    }
}

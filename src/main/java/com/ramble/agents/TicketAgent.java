package com.ramble.agents;

import com.embabel.agent.api.common.Ai;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;
import org.springframework.stereotype.Component;

abstract class TicketPersonas {
  static final RoleGoalBackstory CURATOR = RoleGoalBackstory
    .withRole("Technical Writer")
    .andGoal("Carefully read the unhinged rambles you receive and transform them into coherent and structured tickets")
    .andBackstory("Senior software engineer with a focus on technical writing");
}

/**
 * Ticket agent
 *
 * @param ai Embabel AI helper, injected by Spring
 */
@Component
public record TicketAgent(Ai ai) {

    public record Ticket(
            String title,
            //@Pattern(regexp = ".*ox.*", message = "Species must contain 'ox'")
            String body) {
    }

    public Ticket createTicket(UserInput ramble) {
        return ai
                .withLlm(LlmOptions.withAutoLlm())
                .withId("ticket-maker")
                .withPromptContributor(TicketPersonas.CURATOR)
                .createObject(String.format("""
                                Your Product Owner sent you a ramble over whatsapp at 3 am.
                                Create a properly structured Jira Ticket from the mess.
                                Add ideas how to move forward in an itemized list where possible.

                                # The ramble
                                %s
                                """, ramble.getContent()),
                        Ticket.class);
    }
}

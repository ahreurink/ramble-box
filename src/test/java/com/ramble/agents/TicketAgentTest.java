package com.ramble.agents;

import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.unit.FakeOperationContext;
import com.embabel.agent.testing.unit.FakePromptRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TicketAgentTest {

    @Test
    void listTickets(){

    }

    @Test
    void testCreateTicket()  {
        var context = FakeOperationContext.create();
        var promptRunner = (FakePromptRunner) context.promptRunner();
        context.expectResponse(new TicketAgent.Ticket("Test API", "The response should be correct"));

        var agent = new TicketAgent(context.ai());
        agent.createTicket(new UserInput("Test that sh*t", Instant.now()));

        var prompt = promptRunner.getLlmInvocations()
          .getFirst()
          .getMessages()
          .getFirst()
          .getContent();
        assertTrue(prompt.contains("sh*t"), String.format("Expected prompt to contain the SUT. Was: %s", prompt));
    }
}

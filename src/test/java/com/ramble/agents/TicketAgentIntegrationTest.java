package com.ramble.agents;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.integration.EmbabelMockitoIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Use framework superclass to test the complete workflow of writing a ticket.
 * This will run under Spring Boot against an AgentPlatform instance
 * that has loaded all our agents.
 */
class TicketAgentIntegrationTest extends EmbabelMockitoIntegrationTest {
    @Test
    void shouldExecuteCompleteWorkflow() {
      var ramble = new UserInput("Shoudn't this be tested?");
      var ticket = new TicketAgent.Ticket("Add proper test", "Integration test should work");

      whenCreateObject(prompt -> prompt.contains("be tested"), TicketAgent.Ticket.class)
              .thenReturn(ticket);

      var invocation = AgentInvocation.create(agentPlatform, TicketAgent.Ticket.class);
      TicketAgent.Ticket resulting_ticket = invocation.invoke(ramble);

      assertNotNull(resulting_ticket);
      assertTrue(resulting_ticket.body().contains(ticket.body()),
              "Expected ticket body should be present. Instead is " + resulting_ticket.body());


//      verifyCreateObjectMatching(prompt -> prompt.contains("Shoudn't this be tested?"), TicketAgent.Ticket.class,
//              llm -> llm.getLlm().getTemperature() == 0.7 && llm.getToolGroups().isEmpty());
      verifyGenerateTextMatching(prompt -> prompt.contains("You will be given a short story to review"));
      verifyNoMoreInteractions();
    }
}

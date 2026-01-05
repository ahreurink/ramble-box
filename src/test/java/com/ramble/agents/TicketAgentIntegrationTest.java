package com.ramble.agents;

import com.embabel.agent.api.common.autonomy.AgentInvocation;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.core.ToolGroup;
import com.embabel.agent.core.ToolGroupRequirement;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.testing.integration.EmbabelMockitoIntegrationTest;
import com.ramble.Main;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Use framework superclass to test the complete workflow of writing a ticket.
 * This will run under Spring Boot against an AgentPlatform instance
 * that has loaded all our agents.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TicketAgentIntegrationTest extends EmbabelMockitoIntegrationTest {
    @LocalServerPort
    private int port;

    private String createTicketUrl() {
        return "http://localhost:" + port + "/ramble";
    }

    TestRestTemplate restTemplate = new TestRestTemplate();

    Logger logger = LoggerFactory.getLogger(TicketAgent.class);

    @Test
    void shouldExecuteCompleteWorkflow() {
        String rambletext = "Shoudn't this be tested?";
        var ramble = new UserInput(rambletext);
        var ticket = new TicketAgent.Ticket("Add proper test", "Integration test should work");

        whenCreateObject(prompt -> prompt.contains("sent you a ramble"), TicketAgent.Ticket.class)
            .thenReturn(ticket);

        whenCreateObject(prompt -> prompt.contains("Create a new ticket in Github"), Boolean.class)
            .thenReturn(true);

        HttpEntity<String> postBody = new HttpEntity<>(rambletext, new HttpHeaders());

        var response = restTemplate.exchange(createTicketUrl(), HttpMethod.POST, postBody, String.class);

        logger.info("Status = {}, message = {}", response.getStatusCode(), response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verifyCreateObjectMatching(prompt -> prompt.contains("sent you a ramble"), TicketAgent.Ticket.class,
              llm -> llm.getToolGroups().isEmpty());
        verifyCreateObjectMatching(prompt -> prompt.contains("sent you a ramble"), TicketAgent.Ticket.class,
            llm -> llm.getToolGroups().isEmpty());

        verifyCreateObjectMatching(prompt -> prompt.contains("Create a new ticket in Github"),
            Boolean.class,
            llm -> llm.getToolGroups().contains(new ToolGroupRequirement(CoreToolGroups.GITHUB))
                             && llm.getToolGroups().size() == 1);

        verifyNoMoreInteractions();
    }
}










//class TicketAgentIntegrationTest extends EmbabelMockitoIntegrationTest {
//    @Test
//    void shouldExecuteCompleteWorkflow() {
//        var ramble = new UserInput("Shoudn't this be tested?");
//        var ticket = new TicketAgent.Ticket("Add proper test", "Integration test should work");
//
//        whenCreateObject(prompt -> prompt.contains("be tested"), TicketAgent.Ticket.class)
//            .thenReturn(ticket);
//
//        var invocation = AgentInvocation.create(agentPlatform, TicketAgent.Ticket.class);
//        TicketAgent.Ticket resulting_ticket = invocation.invoke(ramble);
//
//        assertNotNull(resulting_ticket);
//        assertTrue(resulting_ticket.body().contains(ticket.body()),
//            "Expected ticket body should be present. Instead is " + resulting_ticket.body());
//
//
//        verifyCreateObjectMatching(prompt -> prompt.contains("Shoudn't this be tested?"), TicketAgent.Ticket.class,
//              llm -> llm.getToolGroups().isEmpty());
//
//        verifyCreateObjectMatching(prompt -> prompt.contains("Create a new ticket in Github"),
//            Boolean.class, llm -> llm.getToolGroups().contains(CoreToolGroups.GITHUB));
//        verifyNoMoreInteractions();
//    }
//}

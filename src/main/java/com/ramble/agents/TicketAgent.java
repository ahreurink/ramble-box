package com.ramble.agents;

import com.embabel.agent.api.common.Ai;
import com.embabel.agent.api.common.PromptRunner;
import com.embabel.agent.core.CoreToolGroups;
import com.embabel.agent.domain.io.UserInput;
import com.embabel.agent.prompt.persona.RoleGoalBackstory;
import com.embabel.common.ai.model.LlmOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

abstract class TicketPersonas {
    static final RoleGoalBackstory CURATOR = RoleGoalBackstory
        .withRole("Technical Writer")
        .andGoal("Carefully read the unhinged rambles you receive and transform them into coherent and structured tickets." +
            "When done, you need to create the ticket using the MCP tool github-mcp-server that's been made available to you." +
            "Specifically you have access to the ahreurink/ramble-box repository.")
        .andBackstory("Senior software engineer with a focus on technical writing");
}

/**
 * Ticket agent
 */
@Component
public class TicketAgent {
    Logger logger = LoggerFactory.getLogger(TicketAgent.class);

    static final Path promptPath = Path.of(new ClassPathResource("prompts").getPath());

    private final Ai ai;

    TicketAgent(Ai ai) {
        this.ai = ai;
        getDraftTicketPrompt();
    }

    private PromptRunner getPromptRunner() {
        return ai
            .withLlm(LlmOptions.withAutoLlm())
            .withId("ticket-maker")
            .withPromptContributor(TicketPersonas.CURATOR);
    }

    public record Ticket(
        String title,
        //@Pattern(regexp = ".*ox.*", message = "Species must contain 'ox'")
        String body) {
    }

    private final String repo = "repo=ahreurink/ramble-box";

    public Ticket createTicket(UserInput ramble) {
        return getPromptRunner()
            .createObject(String.format(getDraftTicketPrompt(), ramble.getContent()),
                Ticket.class);
    }

    public void listTickets() {
        String response = ai
            .withLlm(LlmOptions.withAutoLlm())
            .withId("ticket-maker")
            .withPromptContributor(TicketPersonas.CURATOR)
            .withToolGroup(CoreToolGroups.GITHUB)
            .generateText(String.format("list_issues\n %s", repo));

        logger.info("Ticket list = \"{}\"", response);
    }

    public Boolean postTicket(Ticket ticket) {
        Boolean succeeded = getPromptRunner()
            .withToolGroup(CoreToolGroups.GITHUB) //Now use "issue_write" mcp tool to create the ticket.
            .createObject(String.format(getPostTicketPrompt(), ticket.title, ticket.body, repo), Boolean.class);
        logger.info("Creation response = \"{}\"", succeeded);
        return succeeded;
    }

    private String getFileContent(String file) {
        var cl = getClass().getResource(file);
        if(cl == null) {
            logger.error("File {} is not available. Resulting to default prompt", file);
            return "Draft me a ticket text. Put in the title \"DEFAULT PROMPT!!\" %s";
        }
        try (var is = cl.openStream()){
            return new String(is.readAllBytes());
        } catch (IOException e) {
            logger.error("File {} is not available. Resulting to default prompt", file);
            return "Draft me a ticket text. Put in the title \"DEFAULT PROMPT!!\" %s";
        }
    }

    private String getDraftTicketPrompt() {
        return getFileContent("/prompts/draftTicket.prompt");
    }

    private String getPostTicketPrompt() {
        return getFileContent("/prompts/postTicket.prompt");
    }
}

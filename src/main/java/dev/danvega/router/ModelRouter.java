package dev.danvega.router;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.question.Choice;
import org.springaicommunity.typesafe.response.ChoiceAnswer;
import org.springframework.stereotype.Service;

/**
 * Picks the cheapest model tier that can still answer a prompt well.
 * <p>
 * The decision is a single Jev {@link Choice} question. Jev returns a label plus a
 * probability for every option, so the caller can see how close the call was.
 */
@Service
public class ModelRouter {

	private static final Logger log = LoggerFactory.getLogger(ModelRouter.class);

	static final String QUESTION = "tier";

	private final TypeSafeClient typeSafeClient;

	private final Choice tierChoice;

	public ModelRouter(TypeSafeClient typeSafeClient) {
		this.typeSafeClient = typeSafeClient;
		Choice.Builder choice = Choice
				.builder()
				.instructions("Which model tier is the cheapest one that can still answer this prompt well? Prefer the cheaper tier unless the prompt clearly needs more capability.");
		for (ModelTier tier : ModelTier.values()) {
			choice.option(tier.name(), tier.description());
		}
		this.tierChoice = choice.build();
	}

	public RoutingDecision route(String prompt) {
		ChoiceAnswer answer = this.typeSafeClient.systemOne(prompt, Map.of(QUESTION, this.tierChoice)).choice(QUESTION);
		ModelTier tier = ModelTier.valueOf(answer.value());
		log.info("Routing to {} with confidence {}", tier.modelId(), answer.confidence());
		return new RoutingDecision(tier, tier.modelId(), answer.confidence(), answer.probabilities());
	}

}

package dev.danvega.router;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springaicommunity.typesafe.TypeSafeClient;
import org.springaicommunity.typesafe.question.Choice;
import org.springaicommunity.typesafe.question.Question;
import org.springaicommunity.typesafe.response.Answer;
import org.springaicommunity.typesafe.response.ChoiceAnswer;
import org.springaicommunity.typesafe.response.SystemOneResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModelRouterTest {

	@Mock
	TypeSafeClient typeSafeClient;

	@Test
	void routesToTheTierJevPicks() {
		Map<String, Double> probabilities = Map.of("LUNA", 0.05, "TERRA", 0.15, "SOL", 0.70, "ASTRA", 0.10);
		when(typeSafeClient.systemOne(anyString(), anyMap())).thenReturn(response(new ChoiceAnswer("SOL", probabilities, 0.70)));

		RoutingDecision decision = new ModelRouter(typeSafeClient).route("Refactor this service into three classes.");

		assertThat(decision.tier()).isEqualTo(ModelTier.SOL);
		assertThat(decision.model()).isEqualTo("gpt-5.6-sol");
		assertThat(decision.confidence()).isEqualTo(0.70);
		assertThat(decision.probabilities()).isEqualTo(probabilities);
	}

	@Test
	void asksJevWithEveryTierAsAnOption() {
		when(typeSafeClient.systemOne(anyString(), anyMap())).thenReturn(response(new ChoiceAnswer("LUNA", Map.of(), 0.9)));

		new ModelRouter(typeSafeClient).route("hi");

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, Question>> questions = ArgumentCaptor.forClass(Map.class);
		verify(typeSafeClient).systemOne(anyString(), questions.capture());
		Choice choice = (Choice) questions.getValue().get(ModelRouter.QUESTION);
		assertThat(choice.criteria()).containsOnlyKeys("LUNA", "TERRA", "SOL", "ASTRA");
	}

	private static SystemOneResponse response(ChoiceAnswer answer) {
		return new SystemOneResponse("jev-latest", Map.<String, Answer>of(ModelRouter.QUESTION, answer), null);
	}

}

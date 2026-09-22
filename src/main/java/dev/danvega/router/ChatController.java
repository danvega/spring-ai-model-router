package dev.danvega.router;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	private final ModelRouter router;

	private final ChatClient chatClient;

	public ChatController(ModelRouter router, ChatClient.Builder chatClientBuilder) {
		this.router = router;
		this.chatClient = chatClientBuilder.build();
	}

	@PostMapping("/chat")
	public ChatResult chat(@RequestBody ChatRequest request) {
		RoutingDecision decision = this.router.route(request.prompt());
		String answer = this.chatClient.prompt()
			.user(request.prompt())
			.options(OpenAiChatOptions.builder().model(decision.model()))
			.call()
			.content();
		return new ChatResult(decision, answer);
	}

	public record ChatRequest(String prompt) {}
	public record ChatResult(RoutingDecision routing, String answer) {}

}

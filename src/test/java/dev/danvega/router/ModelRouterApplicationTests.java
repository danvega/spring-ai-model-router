package dev.danvega.router;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = { "spring.ai.openai.api-key=test-key", "spring.ai.typesafe.api-key=test-key" })
class ModelRouterApplicationTests {

	@Autowired
	ModelRouter modelRouter;

	@Test
	void contextLoads() {
		assertThat(modelRouter).isNotNull();
	}

}

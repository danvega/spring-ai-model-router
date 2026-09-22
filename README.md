# Model Router

A small Spring Boot app that picks the right OpenAI model for each request.

Not every prompt needs the most capable model. A greeting does not need `gpt-6-astra`. A tricky architecture question should not go to `gpt-5.6-luna`. This app asks Jev, through the Spring AI TypeSafe starter, which tier a prompt belongs in. Then it sends the prompt to that model.

## The tiers

| Tier  | Model           | Best for                                               |
|-------|-----------------|--------------------------------------------------------|
| luna  | `gpt-5.6-luna`  | Greetings, one-line facts, simple formatting           |
| terra | `gpt-5.6-terra` | Summaries, explanations, short functions               |
| sol   | `gpt-5.6-sol`   | Multi-step reasoning, code across files, deep analysis |
| astra | `gpt-6-astra`   | Research, architecture, hard debugging, proofs         |

Jev is told to prefer the cheaper tier unless the prompt clearly needs more.

## How it works

1. `ModelRouter` sends the prompt to Jev as one `Choice` question. Each tier is an option with a short description of the prompts it fits.
2. Jev returns a label, a confidence, and a probability for every tier.
3. `ChatController` sets that model on the `ChatClient` call and returns the answer along with the routing decision.

The whole thing is four small classes: `ModelTier`, `RoutingDecision`, `ModelRouter`, and `ChatController`.

## Running it

You need two API keys and JDK 27.

```bash
export OPENAI_API_KEY=...
export TYPESAFE_API_KEY=...
./mvnw spring-boot:run
```

Send a prompt:

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "hello there"}'
```

Try a harder one:

```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Design a multi-region failover strategy for a Postgres cluster."}'
```

The response includes both the answer and the decision, so you can see which model handled it and how confident Jev was. The greeting should route to `luna` and the architecture question to `astra`.

## Tests

```bash
./mvnw test
```

The unit tests mock the Jev client, so they run without any API keys.

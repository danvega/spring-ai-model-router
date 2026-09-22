package dev.danvega.router;

public enum ModelTier {

	LUNA("gpt-5.6-luna",
			"Quick, low-stakes requests: greetings, one-line facts, simple formatting, short rewrites. "
					+ "A small, fast model answers these well."),

	TERRA("gpt-5.6-terra",
			"Everyday tasks: summarize a passage, explain a concept, write a short function, "
					+ "answer a clear question in a few sentences."),

	SOL("gpt-5.6-sol",
			"Complex professional work: multi-step reasoning, code that spans several files, "
					+ "detailed analysis, careful long-form writing."),

	ASTRA("gpt-6-astra",
			"The hardest problems: deep research, system architecture, tricky debugging, formal proofs, "
					+ "long tasks where a mistake is costly.");

	private final String modelId;

	private final String description;

	ModelTier(String modelId, String description) {
		this.modelId = modelId;
		this.description = description;
	}

	public String modelId() {
		return this.modelId;
	}
	public String description() {
		return this.description;
	}

}

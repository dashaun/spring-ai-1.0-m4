package dev.dashaun.service.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
public class ChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatApplication.class, args);
	}

}

@RestController
class MyController {

	private final ChatClient chatClient;

	public MyController(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	@GetMapping("/chatResponse")
	ChatResponse chatResponse(@RequestParam(value = "message", defaultValue = "Can you tell me why its a great time to be a Spring Developer?") String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.chatResponse();
	}

	@GetMapping("/content")
	String content(@RequestParam(value = "message", defaultValue = "Can you tell me why its a great time to be a Spring Developer?") String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.content();
	}

	@GetMapping("/structured")
	StructuredAnswer structured(@RequestParam(value = "message", defaultValue = "Can you tell me why its a great time to be a Spring Developer?") String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.entity(StructuredAnswer.class);
	}


}

record StructuredAnswer(String answer) {};


@RestController
class MemoryController {

	private final ChatClient chatClient;

	public MemoryController(ChatClient.Builder builder, VectorStore vectorStore) {
		this.chatClient = builder
				.defaultAdvisors(
						new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()), // RAG
						new VectorStoreChatMemoryAdvisor(vectorStore, "stackOfTheWeek",5000))
				.build();
	}


	@GetMapping("/DaShaun")
	public String rememberDaShaun(@RequestParam(value = "message", defaultValue = "My name is DaShaun.") String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.content();
	}

	@GetMapping("/whoami")
	public String whoami(@RequestParam(value = "message", defaultValue = "What is my name?") String message) {
		return chatClient.prompt()
				.user(message)
				.call()
				.content();
	}

}
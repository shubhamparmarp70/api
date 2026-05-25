package com.example.demo;

import com.example.demo.model.FinalQueryRequest;
import com.example.demo.model.WebhookRequest;
import com.example.demo.model.WebhookResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	private final RestTemplate restTemplate = new RestTemplate();

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) {

		try {

			WebhookResponse response = generateWebhook();

			if (response == null) {
				System.out.println("Webhook generation failed");
				return;
			}

			System.out.println("Webhook generated successfully");

			// FINAL SQL QUERY

			String finalSqlQuery =
					"SELECT name, salary " +
							"FROM employee " +
							"WHERE salary > 50000";

			submitQuery(
					response.getWebhook(),
					response.getAccessToken(),
					finalSqlQuery
			);

		} catch (Exception e) {

			System.out.println("Error occurred");

			e.printStackTrace();
		}
	}

	private WebhookResponse generateWebhook() {

		String url =
				"https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

		WebhookRequest request = new WebhookRequest();

		request.setName("Your Name");
		request.setRegNo("REG12347");
		request.setEmail("your@email.com");

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<WebhookRequest> entity =
				new HttpEntity<>(request, headers);

		ResponseEntity<WebhookResponse> response =
				restTemplate.postForEntity(
						url,
						entity,
						WebhookResponse.class
				);

		return response.getBody();
	}

	private void submitQuery(
			String webhookUrl,
			String token,
			String sqlQuery
	) {

		FinalQueryRequest request =
				new FinalQueryRequest();

		request.setFinalQuery(sqlQuery);

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.APPLICATION_JSON);

		// IMPORTANT CHANGE HERE

		headers.set("Authorization", token);

		HttpEntity<FinalQueryRequest> entity =
				new HttpEntity<>(request, headers);

		ResponseEntity<String> response =
				restTemplate.postForEntity(
						webhookUrl,
						entity,
						String.class
				);

		System.out.println("Submission Response:");

		System.out.println(response.getBody());
	}
}
package ai.gen.service;

import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Service
public class JobService {

    private final Map<String, String> jobStatusMap = new ConcurrentHashMap<>();
    private final Map<String, String> jobResultMap = new ConcurrentHashMap<>();


    private final String SCRAPE_SERVICE_ENDPOINT = "http://localhost:8000/scrapping/perform";
    private final String PRICING_SERVICE_ENDPOINT = "https://api.example.com/endpoint2";

    private final RestTemplate restTemplate;

    public JobService() {
        this.restTemplate = new RestTemplate();
    }



    @Async
    public void processJobAsync(String jobName, String itemUrl) {
        jobStatusMap.put(jobName, "Pending");

        try {
            String jobResult = simulateJobProcessing(jobName, itemUrl);

            jobStatusMap.put(jobName, "Completed");
            jobResultMap.put(jobName, jobResult);

        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();

            jobStatusMap.put(jobName, "Failed");

        }

        // Once the job is done, you can log or update the job status
        System.out.println("Job " + jobName + " is completed.");
    }

    private String simulateJobProcessing(String jobName, String itemUrl) throws InterruptedException, ExecutionException {
        // Simulate a long-running job (replace with your actual processing logic)
        CompletableFuture<String> result1 = performScrape(itemUrl);
        CompletableFuture<String> result2 = result1.thenCompose(this::getCompetitivePrice);

        Thread.sleep(5000); // Sleep for 5 seconds to simulate processing

        String aggregatedResult = result2.get();

        return aggregatedResult;
    }

    private CompletableFuture<String> performScrape(String itemUrl) {

        return CompletableFuture.supplyAsync(() -> {
            // Simulate API call with different processing times
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                JSONObject requestObj = new JSONObject();
                requestObj.put("url", itemUrl);
                String requestBody = requestObj.toString();
                // Create a POST request with the given request body and headers

                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        SCRAPE_SERVICE_ENDPOINT,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );


                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    return responseEntity.getBody();
                } else {
                    return "API Call failed for " + SCRAPE_SERVICE_ENDPOINT + " with status code: " + responseEntity.getStatusCode();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "API Call failed for " + SCRAPE_SERVICE_ENDPOINT;
            }
        });
    }

    private CompletableFuture<String> getCompetitivePrice(String scrapeResponse) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate API call with different processing times
            try {
                Thread.sleep(3000); // Sleep for 3 seconds to simulate API call
                return scrapeResponse;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "API Call failed for " + PRICING_SERVICE_ENDPOINT;
            }
        });
    }


    public String getJobStatus(String jobName) {
        // Get the job status from the map
        return jobStatusMap.getOrDefault(jobName, "Job not found");
    }

    public String getJobResult(String jobName) {
        // Get the job result from the map
        return jobResultMap.getOrDefault(jobName, "Result not available");
    }
}

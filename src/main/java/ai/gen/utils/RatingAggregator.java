package ai.gen.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class RatingAggregator {

    public static String aggregateRatings(List<String> ratings) {

        if (ratings == null || ratings.isEmpty()) {
            return "0/5"; // Return a default value if the list is empty or null
        }

        System.out.println(ratings.toString());

        double totalNumerator = 0.0;
        int totalDenominator = 0;

        for (String rating : ratings) {
            try {
                // Split the rating string and parse the numerator and denominator as doubles
                String[] parts = rating.split("/");
                double numerator = Double.parseDouble(parts[0]);
                double denominator = Double.parseDouble(parts[1]);

                // Ensure the denominator is a multiple of 5
                if (denominator % 5 != 0) {
                    throw new IllegalArgumentException("Denominator must be a multiple of 5.");
                }

                totalNumerator += numerator; // Adjust numerator based on denominator
                totalDenominator += denominator;
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                // Handle invalid rating formats, missing parts, or invalid denominators
                // You may want to log an error or take appropriate action
            }
        }

        // Calculate the average and format it as a string out of 5
        int ratio = (int)Math.round(totalDenominator)/5;
        double average = (totalNumerator/ratio);
        String aggregatedRating = String.format("%.1f/5", average);

        return aggregatedRating;
    }

    public static String comparePrices(String jsonInput, double sourcePrice) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonInput);

        JsonNode competitorsNode = rootNode.get("competitors");

        if (competitorsNode == null || !competitorsNode.isObject()) {
            return "5/5";
        }

        int numberOfCompetitors = competitorsNode.size();
        int betterCompetitorsCount = 0;

        for (JsonNode competitor : competitorsNode) {
            double competitorPrice = competitor.asDouble();
            if (sourcePrice > competitorPrice) {
                betterCompetitorsCount++;
            }
        }

        double score = (double) betterCompetitorsCount / numberOfCompetitors * 5;

        if (score == 0.0) {
            score = 1.0;
        }
        String formattedScore = String.format("%.1f/5", score);

        return formattedScore;
    }

    public static String calculateRating(int userRating) {
        // Assuming your rating calculation logic here, e.g., mapping userRating to a 5-star scale
        // You can replace this logic with your actual rating computation
        int maxRating = 8;
        int maxStars = 5;

        // Calculate the rating on a 5-star scale
        int scaledRating = (int) Math.round((double) userRating / maxRating * maxStars);

        // Ensure the scaled rating is within the 1 to 5 range
        scaledRating = Math.max(1, Math.min(5, scaledRating));

        return scaledRating + "/5";
    }

}


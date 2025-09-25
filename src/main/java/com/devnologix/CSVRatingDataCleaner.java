package com.devnologix;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class CSVRatingDataCleaner {

    private static String csvFile;
    private static String SAVE_URL;
    private static String TOKEN;

    public CSVRatingDataCleaner() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(input);

            csvFile = props.getProperty("csv.file2");
            SAVE_URL = props.getProperty("rating.url"); // e.g., http://localhost:8080/Rating/save
            TOKEN = props.getProperty("token");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main function to upload ratings
    public void uploadRatings(Map<String, String> restaurantNameIdMap) {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> rows = reader.readAll();
            if (rows.isEmpty())
                return;

            rows.remove(0); // remove header
            Integer count = 1;
            for (String[] row : rows) {
                if (row.length < 4)
                    continue;

                String restaurantName = row[0].trim();
                String authorName = row[1].trim();
                String ratingStr = row[2].trim();
                String reviewText = row[3].trim();

                String restaurantId = restaurantNameIdMap.get(restaurantName);
                if (restaurantId == null) {
                    System.out.println("Restaurant not found: " + restaurantName);
                    continue;
                }

                String userId = RestaurantUtils.generateEmail(authorName);

                double stars = 0;
                try {
                    stars = Double.parseDouble(ratingStr);
                } catch (Exception e) {
                    stars = 0;
                }

                // Build JSON
                String json = "{"
                        + "\"user_id\":\"" + userId + "\","
                        + "\"review\":\"" + reviewText.replace("\"", "\\\"") + "\","
                        + "\"stars\":" + stars + ","
                        + "\"dish_id\":" + restaurantId + ","
                        + "\"resturant_id\":" + restaurantId
                        + "}";

                System.out.println("This is the count " + count);

                // Before sending the POST request
                if (RestaurantUtils.isDuplicate(userId, reviewText, stars, Integer.parseInt(restaurantId),
                        Integer.parseInt(restaurantId))) {
                    System.out.println("Duplicate rating skipped for user: " + userId);
                    continue;
                }

                // Send POST request

                sendPostRequest(json);
                count++;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPostRequest(String json) {
        try {
            URL url = new URL(SAVE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("Saved rating successfully.");
            } else {
                System.out.println("Failed to save rating | Response code: " + responseCode);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        CSVRatingDataCleaner uploader = new CSVRatingDataCleaner();

        // Example: Map restaurant names to IDs (replace with DB fetch)
        Map<String, String> restaurantMap = RestaurantUtils.getRestaurantNameIdMap();
        // restaurantMap.put("Dominos Pizza", "2");
        // restaurantMap.put("Pizza Hut", "3");

        uploader.uploadRatings(restaurantMap);
    }
}

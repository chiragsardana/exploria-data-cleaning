package com.devnologix;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UserRegistrar {

    private static String SAVE_URL = "http://localhost:8080/users/register"; // replace with actual host:port

    // Send POST requests to register users
    public static void registerUsers(List<String> userIds) {
        int count = 1;
        for (String userId : userIds) {
            try {
                String username = userId;
                String email = userId;
                String password = "abc123";
                String name = UserUtils.removeExploriaSuffix(userId);
                String phone = UserUtils.generatePhone();
                String businessTitle = "Manually Created";

                // Build JSON
                String json = "{"
                        + "\"username\":\"" + username + "\","
                        + "\"email\":\"" + email + "\","
                        + "\"password\":\"" + password + "\","
                        + "\"name\":\"" + name.replace("\"", "\\\"") + "\","
                        + "\"phone\":\"" + phone + "\","
                        + "\"businessTitle\":\"" + businessTitle + "\""
                        + "}";
                System.out.println("The user count is "+count);
                sendPostRequest(json);
                count++;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendPostRequest(String json) {
        try {
            URL url = new URL(SAVE_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                System.out.println("User registered successfully: " + json);
            } else {
                System.out.println("Failed to register user | Response code: " + responseCode + " | " + json);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        // Fetch distinct user_ids from the rating table
        List<String> userIds = UserUtils.getDistinctUserIds();
        System.out.println("THe count for the use ris "+userIds.size());
        // Register users
        registerUsers(userIds);
    }
}

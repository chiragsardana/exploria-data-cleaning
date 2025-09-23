package com.devnologix;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class CSVRestaurantDataCleaner {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String csvFile;
    private static String SAVE_URL;
    private static String TOKEN;

    public CSVRestaurantDataCleaner() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(input);

            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
            // tableName = props.getProperty("db.table");
            csvFile = props.getProperty("csv.file1");
            SAVE_URL = props.getProperty("url");
            TOKEN = props.getProperty("token"); // Replace with your actual token

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CleanedRestaurant {
        public String name;
        public String description;
        public Double rating;
        public List<String> cuisines;
        public Integer costForOne;
        public Integer deliveryTime;
        public List<String> imageUrls = new ArrayList<>();
        public List<String> dishes = new ArrayList<>();

        @Override
        public String toString() {
            // Escape quotes in strings
            String safeName = name.replace("\"", "\\\"");
            String safeDescription = description.replace("\"", "\\\"");

            // Convert lists to JSON array format
            String cuisinesJson = cuisines != null
                    ? "[" + cuisines.stream().map(s -> "\"" + s.replace("\"", "\\\"") + "\"")
                            .collect(Collectors.joining(",")) + "]"
                    : "[]";

            String imagesJson = imageUrls != null
                    ? "[" + imageUrls.stream().map(s -> "\"" + s.replace("\"", "\\\"") + "\"")
                            .collect(Collectors.joining(",")) + "]"
                    : "[]";

            // Build JSON string
            return "{"
                    + "\"name\":\"" + safeName + "\","
                    + "\"description\":\"" + safeDescription + "\","
                    + "\"rating\":" + rating + ","
                    + "\"costForOne\":" + costForOne + ","
                    + "\"deliveryTime\":" + deliveryTime + ","
                    + "\"cuisines\":" + cuisinesJson + ","
                    + "\"image_urls\":" + imagesJson
                    + "}";
        }
        /*
         * 
         * {
         * "name": "Domino's Pizza",
         * "description": "Best pizza in town",
         * "rating": 4.3,
         * "costForOne": 300,
         * "deliveryTime": 30,
         * "image_urls": [
         * "https://b.zmtcdn.com/data/pictures/9/20223399/f213cc2a1c30b7fc51af0bab24307bab.jpeg"
         * ],
         * "dishes": [],
         * "cuisines": [
         * "South Indian",
         * "Italian",
         * "North Indian"
         * ]
         * }
         * 
         */
    }

    public List<CleanedRestaurant> cleanData() {
        List<CleanedRestaurant> cleanedList = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
            List<String[]> rows = reader.readAll();
            if (rows.isEmpty())
                return cleanedList;

            // Remove header
            rows.remove(0);

            Integer avgCost = 0;

            Integer avgDeliveryTime = 0;

            ArrayList<String> imageURL1 = imagesUrl();

            Integer j = 0;

            Integer i = 0;

            for (String[] row : rows) {

                if (j == imageURL1.size() - 1) {
                    j = 0;
                } else {
                    j++;
                }
                // Ignoring the Row with missing any column
                if (row.length < 6)
                    continue;

                CleanedRestaurant r = new CleanedRestaurant();
                r.name = row[1].trim();

                r.description = row[3].trim();

                // Rating
                try {
                    r.rating = Double.parseDouble(row[2].trim());
                } catch (Exception e) {
                    r.rating = 0.0;
                }

                // Cuisines
                r.cuisines = new ArrayList<>();
                if (row[3] != null && !row[3].isEmpty()) {
                    String[] arr = row[3].split(",");
                    for (String s : arr)
                        r.cuisines.add(s.trim());
                }

                // CostForOne
                try {
                    r.costForOne = Integer.parseInt(row[4].replaceAll("[^0-9]", ""));
                    avgCost += r.costForOne;
                } catch (Exception e) {
                    r.costForOne = (int) avgCost / i;
                    // r.costForOne = -71;
                }

                // DeliveryTime
                try {
                    r.deliveryTime = Integer.parseInt(row[5].replaceAll("[^0-9]", ""));
                    avgDeliveryTime += r.deliveryTime;
                } catch (Exception e) {
                    r.deliveryTime = avgDeliveryTime / i;
                }
                r.imageUrls.add(imageURL1.get(j));
                cleanedList.add(r);
                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cleanedList;
    }

    public static void main(String[] args) {
        CSVRestaurantDataCleaner cleaner = new CSVRestaurantDataCleaner();
        List<CleanedRestaurant> cleanedList = cleaner.cleanData();
        System.out.println("The Total Restaurant in the list is" + cleanedList.size());
        for (CleanedRestaurant cleanedRestaurant : cleanedList) {
            System.out.println(cleanedRestaurant.toString());
        }
        saveRestaurants(cleanedList);
        // cleaner.insertIntoDB(cleanedList);
    }

    public ArrayList<String> imagesUrl() {
        ArrayList<String> imagesURL = new ArrayList<>();
        imagesURL.add(
                "https://thumbs.dreamstime.com/b/chinese-restaurant-exterior-red-lanterns-asian-eatery-cultural-dining-city-vector-design-generative-ai-charming-adorned-374502203.jpg");
        imagesURL.add(
                "https://static.vecteezy.com/system/resources/previews/041/761/126/non_2x/spanish-restaurant-illustration-with-various-of-food-menu-traditional-dish-typical-recipe-and-cuisine-in-flat-cartoon-background-design-vector.jpg");
        imagesURL.add(
                "https://static.vecteezy.com/system/resources/previews/041/761/035/non_2x/italian-food-restaurant-or-cafeteria-illustration-with-traditional-italy-dishes-pizza-or-pasta-in-flat-cartoon-background-design-vector.jpg");
        imagesURL.add(
                "https://www.citypng.com/public/uploads/preview/hd-illustration-cartoon-burger-restaurant-png-701751694871047vb76rordrd.png");
        imagesURL.add(
                "https://easydrawingguides.com/wp-content/uploads/2022/11/how-to-draw-a-restaurant-featured-image-1200.png");
        imagesURL.add(
                "https://png.pngtree.com/png-clipart/20210129/ourmid/pngtree-line-drawing-restaurant-restaurant-png-image_2844098.jpg");
        imagesURL.add(
                "https://thumbs.dreamstime.com/b/restaurant-team-chef-cook-manager-waiter-vector-illustratio-illustration-flat-design-74166175.jpg");
        return imagesURL;
    }
    public static void saveRestaurants(List<CleanedRestaurant> restaurants) {
        for (CleanedRestaurant r : restaurants) {
            try {
                URL url = new URL(SAVE_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                conn.setDoOutput(true);

                // Use the toString() method to generate JSON
                String json = r.toString();

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    System.out.println("Saved restaurant: " + r.name);
                } else {
                    System.out.println("Failed to save: " + r.name + " | Response code: " + responseCode);
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
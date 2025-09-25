package com.devnologix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.io.InputStream;

public class RestaurantUtils {

    //
    // db.url=jdbc:mysql://localhost:3306/exploria
    // dbPassword=sardana80
    // dbUser=root
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try {
            Properties props = new Properties();
            InputStream input = RestaurantUtils.class.getClassLoader().getResourceAsStream("application.properties");
            props.load(input);

            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
            System.out.println(dbPassword);
            System.out.println(dbUser);
            System.out.println(dbUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the Restaurant table and returns a map of name -> id
     */
    public static Map<String, String> getRestaurantNameIdMap() {
        Map<String, String> map = new HashMap<>();

        String query = "SELECT id, name FROM resturant_sirsa"; // Adjust table/column names if different

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name").trim();
                map.put(name, id);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String generateEmail(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        // Remove all spaces and append the domain
        String email = name.replaceAll("\\s+", "") + "@exploria.com";
        return email.toLowerCase(); // optional: make it lowercase
    }

    public static boolean isDuplicate(String userId, String review, double stars, int dishId, int restaurantId) {
        
        String query = "SELECT COUNT(*) FROM Rating_sirsa WHERE user_id=? AND review=? AND stars=? AND dish_id=? AND resturant_id=?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, userId);
            ps.setString(2, review);
            ps.setDouble(3, stars);
            ps.setInt(4, dishId);
            ps.setInt(5, restaurantId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Example usage
    public static void main(String[] args) {
        Map<String, String> restaurantMap = getRestaurantNameIdMap();
        System.out.println("This is the Size of the Map " + restaurantMap.size());
        // restaurantMap.forEach((name, id) -> System.out.println(name + " -> " + id));
        System.out.println("This is the name Chirag Sardana " + generateEmail("Chirag Sardana"));
    }
}

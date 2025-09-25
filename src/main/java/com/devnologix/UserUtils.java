package com.devnologix;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.io.InputStream;

public class UserUtils {

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        try {
            Properties props = new Properties();
            InputStream input = UserUtils.class.getClassLoader().getResourceAsStream("application.properties");
            props.load(input);

            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.username");
            dbPassword = props.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch all distinct user_ids from rating_sirsa table
     */
    public static List<String> getDistinctUserIds() {
        List<String> userIds = new ArrayList<>();
        String query = "SELECT DISTINCT user_id FROM rating_sirsa";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                userIds.add(rs.getString("user_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userIds;
    }

    /**
     * Remove "@exploria.com" suffix from a string if present
     */
    public static String removeExploriaSuffix(String input) {
        if (input != null && input.endsWith("@exploria.com")) {
            return input.substring(0, input.length() - "@exploria.com".length());
        }
        return input;
    }
    // Generate random 10-digit phone number
    public static String generatePhone() {
        Random random = new Random();
        StringBuilder phone = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            phone.append(random.nextInt(10));
        }
        return phone.toString();
    }
    // Example usage
    public static void main(String[] args) {
        List<String> users = getDistinctUserIds();
        System.out.println("Distinct users: " + users.size());

        String test = "johnDoe@exploria.com";
        System.out.println("Removed suffix: " + removeExploriaSuffix(test)+ generatePhone());
    }
    
}

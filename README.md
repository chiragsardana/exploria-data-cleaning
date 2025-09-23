

---

# Restaurant Data Cleaner & API Uploader

This project provides a **Java Maven tool** to read restaurant data from a CSV file, clean the data, and send it to a **Spring Boot backend API** for storage. It also handles **cuisines, images, and other restaurant features** properly.

---

## **1. CSV File Structure**

The CSV file should have the following columns:

| Column       | Description                                               | Example                        |
| ------------ | --------------------------------------------------------- | ------------------------------ |
| ID           | Unique restaurant ID (optional if backend auto-generates) | 1                              |
| Name         | Restaurant name                                           | Social Heights                 |
| Rating       | Average rating (float)                                    | 4.1                            |
| Cuisine      | Comma-separated cuisines                                  | Chinese, Pizza, Burger         |
| CostForOne   | Cost for one person (numeric)                             | 250                            |
| DeliveryTime | Delivery time in minutes                                  | 34                             |
| ImageUrls    | Comma-separated image URLs                                | img1.jpg, img2.jpg             |
| Description  | Optional description of the restaurant                    | Best place for Pizza and more! |

**Sample CSV Header:**

```
ID,Name,Rating,Cuisine,CostForOne,DeliveryTime,ImageUrls,Description
```

> Notes:
>
> * Use `|` or `,` as separators for **multiple cuisines/images** consistently.
> * Remove any invalid characters to avoid parsing errors.

---

## **2. Java Class Structure**

### **CleanedRestaurant.java**

```java
public class CleanedRestaurant {
    public String name;
    public String description;
    public Double rating;
    public Integer costForOne;
    public Integer deliveryTime;
    public List<String> cuisines;
    public List<String> imageUrls;

    @Override
    public String toString() {
        // Returns JSON string for API POST request
    }
}
```

### **CSVDataCleaner.java**

* Reads CSV file.
* Cleans data:

  * Converts rating to float.
  * Parses cost and delivery time to integers.
  * Splits cuisines and images into lists.
  * Handles missing/invalid values.
* Returns `List<CleanedRestaurant>`.

---

## **3. API Upload Function**

### **RestaurantUploader.java**

```java
public class RestaurantUploader {

    private static final String SAVE_URL = "http://localhost:8080/Resturant/save";
    private static final String TOKEN = "your_token_here";

    public static void saveRestaurants(List<CleanedRestaurant> restaurants) {
        for (CleanedRestaurant r : restaurants) {
            // Converts r.toString() to JSON
            // Sends POST request with Authorization header
            // Handles response
        }
    }
}
```

> Notes:
>
> * Replace `your_token_here` with your actual API token.
> * The function sends each restaurant individually to the backend API.
> * `toString()` generates a JSON string compatible with your Spring Boot `Restaurant` entity.

---




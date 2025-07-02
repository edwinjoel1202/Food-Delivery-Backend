package com.terminators.FoodDelivery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DistanceService {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @Value("${delivery.fee.per.km}")
    private Double deliveryFeePerKm;

    private final RestTemplate restTemplate;

    public DistanceService() {
        this.restTemplate = new RestTemplate();
    }

    public Double calculateDeliveryFee(Double customerLat, Double customerLon, Double restaurantLat, Double restaurantLon) {
        if (customerLat == null || customerLon == null || restaurantLat == null || restaurantLon == null) {
            throw new RuntimeException("Coordinates are missing for customer or restaurant");
        }

        // Construct Google Maps Distance Matrix API URL
        String origins = customerLat + "," + customerLon;
        String destinations = restaurantLat + "," + restaurantLon;
        String url = String.format(
                "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                origins, destinations, googleMapsApiKey
        );

        // Call Google Maps API
        GoogleDistanceMatrixResponse response = restTemplate.getForObject(url, GoogleDistanceMatrixResponse.class);
        if (response == null || response.rows == null || response.rows.isEmpty() ||
                response.rows.get(0).elements == null || response.rows.get(0).elements.isEmpty()) {
            throw new RuntimeException("Failed to retrieve distance from Google Maps API");
        }

        GoogleDistanceMatrixResponse.Element element = response.rows.get(0).elements.get(0);
        if (!"OK".equals(element.status)) {
            throw new RuntimeException("Distance calculation failed: " + element.status);
        }

        // Extract distance in kilometers (distance.value is in meters)
        double distanceInKm = element.distance.value / 1000.0;
        // Calculate delivery fee
        return distanceInKm * deliveryFeePerKm;
    }

    // DTO classes for Google Maps API response
    public static class GoogleDistanceMatrixResponse {
        public List<Row> rows;

        public static class Row {
            public List<Element> elements;
        }

        public static class Element {
            public Distance distance;
            public String status;
        }

        public static class Distance {
            public long value; // Distance in meters
        }
    }
}
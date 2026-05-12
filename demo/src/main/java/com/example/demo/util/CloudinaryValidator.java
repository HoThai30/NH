package com.example.demo.util;

import java.net.URL;

/**
 * Utility class for validating Cloudinary URLs and image URLs
 */
public class CloudinaryValidator {

    /**
     * Validate if a string is a valid URL (Cloudinary or any image URL)
     * @param url The URL to validate
     * @return true if valid URL, false otherwise
     */
    public static boolean isValidImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        url = url.trim();

        try {
            new URL(url);
            // Additional checks for image URLs
            return url.startsWith("http://") || url.startsWith("https://");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate if URL is from Cloudinary
     * @param url The URL to validate
     * @return true if URL is from Cloudinary, false otherwise
     */
    public static boolean isCloudinaryUrl(String url) {
        if (!isValidImageUrl(url)) {
            return false;
        }
        return url.contains("cloudinary.com") || url.contains("res.cloudinary.com");
    }

    /**
     * Check if string is a valid image URL or empty (for optional images)
     * @param url The URL to validate
     * @return true if empty or valid URL, false otherwise
     */
    public static boolean isValidImageUrlOrEmpty(String url) {
        if (url == null || url.trim().isEmpty()) {
            return true; // Empty is allowed for optional images
        }
        return isValidImageUrl(url);
    }
}

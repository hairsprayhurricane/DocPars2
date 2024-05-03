package org.example;

import jdk.internal.icu.text.UnicodeSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            ArrayList<String> links = new ArrayList<>();
            String url = "https://www.flickr.com/search/?text=photo";

            Document doc = Jsoup.connect(url).get();
            Elements images = doc.select("img");

            System.out.println("Start");

            if (images.isEmpty()) {
                System.out.println("No images found on the page.");
            } else {
                for (Element image : images) {
                    String imageUrl = image.attr("abs:src");
                    System.out.println("Image URL: " + imageUrl);
                    links.add(imageUrl);
                    try {
                        download(imageUrl, "data/" + getUniqueFilename(imageUrl));
                        System.out.println("Downloaded: " + imageUrl);
                    } catch (IOException e) {
                        System.out.println("Failed to download: " + imageUrl);
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Finish");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getUniqueFilename(String url) {
        String filename = getFilename(url);
        Path filePath = Paths.get("data/" + filename);
        int count = 1;
        while (Files.exists(filePath)) {
            String newFilename = filename.substring(0, filename.lastIndexOf('.'))
                    + "_" + count
                    + filename.substring(filename.lastIndexOf('.'));
            filePath = Paths.get("data/" + newFilename);
            count++;
        }
        return filePath.getFileName().toString();
    }

    public static String getFilename(String url) {
        String regex = "[^/]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "xd";
        }
    }

    public static void download(String url, String filePath) throws IOException {
        URL imageURL = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();

            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.copy(inputStream, path);

            inputStream.close();
            connection.disconnect();
        } else {
            throw new IOException("Failed to download image: " + connection.getResponseCode());
        }
    }
}





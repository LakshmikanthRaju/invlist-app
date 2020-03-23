package com.example.invlist.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTPClient {

    private static String extractResponse(HttpsURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine = null;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();

        return response.toString();
    }

    public static String getResponse(String url) {

        try {
            URL urlObj = new URL(url);

            HttpsURLConnection con = (HttpsURLConnection) urlObj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                return "Error: Failed to get value for " + url;
            }
            return extractResponse(con);

        } catch (IOException e) {
            return "Error: Failed to execute for " + url;
        }
    }
}

package de.legoshi.replaymod.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPHelper {

    public static String[] getStrings(String uuid) {
        String[] results = new String[2];
        try {
            URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            connection.disconnect();

            String s = content.toString();
            JSONObject jsonObject = new JSONObject(s);
            String value = jsonObject.getJSONArray("properties").getJSONObject(0).getString("value");
            String signature = jsonObject.getJSONArray("properties").getJSONObject(0).getString("signature");
            results[0] = value;
            results[1] = signature;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

}

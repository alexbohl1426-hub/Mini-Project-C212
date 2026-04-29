import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

class WeatherForecast {

    public static void main(String[] args) throws IOException {

        String url = "https://api.open-meteo.com/v1/forecast?latitude=39.168804&longitude=-86.536659&hourly=temperature_2m&temperature_unit=fahrenheit&timezone=EST";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() != 200) {
            throw new IOException("Request failed: " + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            jsonString.append(line);
        }
        in.close();

        JsonObject jsonStringJson = JsonParser.parseString(jsonString.toString()).getAsJsonObject();
        JsonArray jsonTimes = jsonStringJson.get("hourly").getAsJsonObject().get("time").getAsJsonArray();
        JsonArray jsonTemps = jsonStringJson.get("hourly").getAsJsonObject().get("temperature_2m").getAsJsonArray();

        System.out.println("7-Day Forecast in Fahrenheit:");

        // find where "now" lines up in the hourly array
        int hourNow = LocalDateTime.now().getHour();
        int i = 0;
        while (i < jsonTimes.size() && Integer.parseInt(jsonTimes.get(i).getAsString().substring(11, 13)) != hourNow) {
            i++;
        }

        String lastDate = "";
        while (i < jsonTimes.size()) {
            String stamp = jsonTimes.get(i).getAsString();
            String date = stamp.substring(0, 10);
            String time = stamp.substring(11, 16);

            if (!date.equals(lastDate)) {
                System.out.println("Forecast for " + date + ":");
                lastDate = date;
            }

            double temp = jsonTemps.get(i).getAsDouble();
            System.out.println("    " + time + ": " + String.format("%.1f", temp) + "°F");

            i += 3;
        }
    }
}

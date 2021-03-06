import javax.json.Json;  
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {
    private final static String WeatherURL =  "http://dataservice.accuweather.com/forecasts/v1/daily/5day/295212"; // URL для запроса погоды на 5 дней в СПБ.
    private final static String API_KEY = "?apikey=unY7HYARI7OQOtsPlnAHOO7MwB3nVAJo"; // API ключ для запроса. См. гайд, как его получить и вставить.
    private final static String IS_METRIC = "&metric=true";

    public static void main(String[] args) {
        String forecastJson = load5DayForecastOrNull();

        if (forecastJson != null) {
            StringReader forecastJsonReader = new StringReader(forecastJson);
            JsonReader jsonReader = Json.createReader(forecastJsonReader);
            JsonObject weatherResponseJson = jsonReader.readObject();
            WeatherResponse weatherResponse = new WeatherResponse(weatherResponseJson);
            System.out.println(weatherResponse);
        } else {
            System.out.println("Не удалось прочитать данные с сервера.");
        }
    }

    public static String load5DayForecastOrNull() {
        try {
            // Сформировали URL для запроса к серверу.
            URL weatherUrl = new URL(WeatherURL+API_KEY + IS_METRIC);
            // К серверу постучались.
            HttpURLConnection urlConnection = (HttpURLConnection) weatherUrl.openConnection();
            // getResponseCode отправляет запрос к серверу по указанному нами URL, который по факту является GET запросом.
            if (urlConnection.getResponseCode() == 200) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) { // Как было на предыдущих занятиях, чтобы считать данные из
                    StringBuilder responseContent = new StringBuilder();                                                  // сети, необходимо открыть Stream для их чтения.
                    String line = "";                                                                                     // Тут мы используем для работы с сетью BufferedReader.
                    while ((line = reader.readLine()) != null) { // Считываем данные от сервера до конца (тут нет EOF, как в случае с фалами. Если данных нет от сервера, то метод readLine()
                        responseContent.append(line);            // вернет null.
                    }
                    return responseContent.toString();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }
}

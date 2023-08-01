package manager.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KvsKlient {
    private final int port = 8078;
    private final String host = "localhost";
    private final String tokken;

    public KvsKlient() {
        try {
            //String uriString = "http://localhost:8078/";
            String uriString = String.format("http://%s:%d/register/?API_TOKEN=DEBUG", host, port);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request =HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            tokken = response.body();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }

    }

    public void saveState(String key, String value) {
        try {
            String uriString = String.format("http://%s:%d/save/%s?%s", host, port, key, tokken);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request =HttpRequest.newBuilder().uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(value)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении состояния на KVServer");
        }

    }

    public String loadState(String key) {
        String responseStr;
        try {
            String uriString = String.format("http://%s:%d/load/%s?%s", host, port, key, tokken);
            HttpClient client = HttpClient.newHttpClient();
            URI uri = new URI(uriString);
            HttpRequest request =HttpRequest.newBuilder().uri(uri)
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseStr = response.body();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке состояния из KVServer");
        }


        return responseStr ;
    }
}

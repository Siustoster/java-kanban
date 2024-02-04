package Managers.Server;

import Exceptions.BadUrlException;
import Exceptions.ManagerLoadException;
import Exceptions.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String ERROR_URL = "Адрес не соответствует формату URL";
    private final String serverUrl;
    private final String apiToken;

    public KVTaskClient(String serverUrl) {
        this.serverUrl = serverUrl;
        URI registerUrl = URI.create(serverUrl + "/register");

        try {
            HttpRequest request = HttpRequest.newBuilder().GET().uri(registerUrl).build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response;

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    apiToken  = response.body();
                } else {
                    throw new IOException();
                }
            }  catch (IOException | InterruptedException e) {
                throw new ManagerLoadException("Ошибка регистрации на сервере");
            }
        } catch (IllegalArgumentException e) {
            throw new BadUrlException(ERROR_URL + ": " + registerUrl);
        }
    }

    public void put(String key, String json) {
        URI uri = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .headers("Content-Type", "text/plain;charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response;

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new IOException();
                }
            } catch (IOException | InterruptedException e) {
                throw new ManagerSaveException("Ошибка при сохранении данных на сервер");
            }
        } catch (IllegalArgumentException e) {
            throw new BadUrlException(ERROR_URL + ": " + uri);
        }
    }

    public String load(String key) {
        URI uri = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken);

        String managerData;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response;

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    managerData = response.body();
                } else if (response.statusCode() == 404) {
                    managerData = "";
                } else {
                    throw new IOException();
                }
            } catch (IOException | InterruptedException e) {
                throw new ManagerLoadException("Ошибка при чтении данных с сервера");
            }
        } catch (IllegalArgumentException e) {
            throw new BadUrlException(ERROR_URL + ": " + uri);
        }

        return managerData;
    }
}
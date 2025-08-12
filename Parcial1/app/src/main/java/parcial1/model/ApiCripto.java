
package parcial1.model;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ApiCripto {

    public static void main(String[] args) throws Exception {

        String apiUrl = "https://api.coinlore.net/api/tickers/";

        try {
            // Crear cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Crear solicitud HTTP
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            // Enviar solicitud y obtener respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Analizar la respuesta JSON
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

            // Extraer la lista de criptomonedas
            criptoMoneda[] criptomonedasArray = gson.fromJson(
                    jsonResponse.getAsJsonArray("data"), criptoMoneda[].class);

            // Convertir el array a una lista
            List<criptoMoneda> criptomonedas = Arrays.asList(criptomonedasArray);

                        


        } catch (Exception e) {
            System.err.println("Error al consumir la API: " + e.getMessage());
        }
    }

}





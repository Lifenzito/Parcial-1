
package parcial1.model;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ApiCripto {

    private String apiUrl;

    public ApiCripto(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<criptoMoneda> Obtener() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);

        criptoMoneda[] criptomonedasArray = gson.fromJson(
                jsonResponse.getAsJsonArray("data"), criptoMoneda[].class);

        return Arrays.asList(criptomonedasArray);
    }

    public static void main(String[] args) throws Exception {
        ApiCripto api = new ApiCripto("https://api.coinlore.net/api/tickers/");
        List<criptoMoneda> criptomonedas = api.Obtener();
        
        System.out.println("Se obtuvieron " + criptomonedas.size() + " criptomonedas");
        if (!criptomonedas.isEmpty()) {
            System.out.println("Ejemplo: " + criptomonedas.get(0));
        }
    }
    

    public void fluctuate(List<criptoMoneda> coins, double pct, Random rnd) {
        for (criptoMoneda c : coins) {
            double price = Double.parseDouble(c.getPrice_usd());
            double factor = 1.0 + (rnd.nextDouble() * 2 * pct - pct);
            double newPrice = Math.max(0.0001, price * factor);
            c.setPrice_usd(String.format("%.6f", newPrice));
        }
    }

}
package parcial1.model;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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

    private static final Logger logger = LogManager.getLogger(ApiCripto.class.getName());


    private String apiUrl;

    public ApiCripto(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<criptoMoneda> Obtener() throws Exception {
        try {
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
            logger.info("Criptomonedas obtenidas correctamente desde la API");
            return Arrays.asList(criptomonedasArray);
        } catch (Exception e) {
            logger.error("Error al obtener criptomonedas: " + e.getMessage(),e);
            throw new IllegalArgumentException("No se pudo obtener la lista de criptomonedas", e);
        }
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
            try {
                double price = Double.parseDouble(c.getPrice_usd());
                double factor = 1.0 + (rnd.nextDouble() * 2 * pct - pct);
                double newPrice = Math.max(0.0001, price * factor);
                c.setPrice_usd(String.format("%.6f", newPrice));
            } catch (Exception e) {
                logger.error("Error fluctuando precio de moneda " + c.getSymbol() + ": " + e.getMessage(),e);
                throw new IllegalArgumentException("Error fluctuando precio de moneda: " + c.getSymbol(), e);
            }
        }
        logger.info("Precios de criptomonedas fluctuados correctamente");
    }

}
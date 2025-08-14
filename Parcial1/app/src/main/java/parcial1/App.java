
package parcial1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import parcial1.model.ApiCripto;
import parcial1.model.criptoMoneda;
import parcial1.model.orderBook;
import parcial1.model.trader;
import parcial1.model.transaccion;
import parcial1.service.TransactionProcessor;

public class App {

    private static final Logger logger = LogManager.getLogger(App.class.getName());

    public static void main(String[] args) {



        try {

            logger.info("Iniciando simulación de mercado de criptomonedas");
            ApiCripto api = new ApiCripto("https://api.coinlore.net/api/tickers/");
            logger.info("Creando instancia de ApiCripto");
            List<criptoMoneda> lista = api.Obtener();
            logger.info("Lista de criptomonedas obtenida. Total: " + lista.size());

            criptoMoneda[] coins = pick10Random(lista);
            logger.info("Se seleccionaron 10 criptomonedas aleatorias para la simulación");

            trader alice = new trader("u1", "Alice", 5_000_000, 4000);
            trader bob = new trader("u2", "Bob", 8_000_000, 4000);
            trader[] traders = {alice, bob};
            logger.info("Traders creados: Alice y Bob");

            orderBook book = new orderBook();
            TransactionProcessor processor = new TransactionProcessor();
            logger.info("OrderBook y TransactionProcessor inicializados");

            Random rnd = new Random();
            logger.info("=== INICIANDO SIMULACIÓN ===");
            System.out.println("=== INICIANDO SIMULACIÓN ===");
            for (int turno = 1; turno <= 10; turno++) {
                logger.info("\n--- TURNO " + turno + " ---");
                System.out.println("\n--- TURNO " + turno + " ---");

                fluctuatePrices(coins, 0.05, rnd);
                logger.info("Precios de criptomonedas fluctuados para el turno " + turno);

                System.out.println("Precios actuales:");
                for (int i = 0; i < Math.min(3, coins.length); i++) {
                    criptoMoneda c = coins[i];
                    System.out.println("  " + c.getSymbol() + ": $" + c.getPrice_usd());
                }

                for (trader t : traders) {
                    logger.info("Creando orden aleatoria para trader: " + t.getname());
                    createRandomOrder(t, coins, book, rnd);
                }

                logger.info("Procesando órdenes del turno " + turno);
                System.out.println("Procesando órdenes:");
                while (!book.isEmpty()) {
                    processor.processOrder(book, traders, coins);
                }
            }

            logger.info("=== FIN SIMULACIÓN ===");
            System.out.println("\n=== RESUMEN FINAL ===");
            for (trader t : traders) {
                System.out.println(t);
                System.out.println("  Portafolio:");
                t.getWallet().forEach(holding -> System.out.println("    " + holding));
                System.out.println("  Total transacciones: " + t.getHistory().size());
                System.out.println();
            }

            try {
                ArrayList<Map<String, Object>> reporte = new ArrayList<>();
                for (trader t : traders) {
                    Map<String, Object> traderInfo = new HashMap<>();
                    traderInfo.put("nombre", t.getname());
                    traderInfo.put("saldo_cop", t.getBalance());
                    traderInfo.put("transacciones", t.getHistory());
                    ArrayList<Map<String, Object>> portafolio = new ArrayList<>();
                    t.getWallet().forEach(holding -> {
                        Map<String, Object> h = new HashMap<>();
                        h.put("symbol", holding.getSymbol());
                        h.put("cantidad", holding.getQuantity());
                        portafolio.add(h);
                    });
                    traderInfo.put("portafolio", portafolio);
                    reporte.add(traderInfo);
                }
                Map<String, Object> resumen = new HashMap<>();
                resumen.put("reporte_final", reporte);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                try (FileWriter writer = new FileWriter("reporte_final.json")) {
                    gson.toJson(resumen, writer);
                }
                logger.info("Archivo reporte_final.json generado correctamente");
            } catch (IOException e) {
                logger.error("Error generando reporte_final.json: " + e.getMessage(), e);
            }
            

        } catch (Exception e) { 
            logger.error("Error en la simulación: " + e.getMessage(),e);
            System.out.println("Ocurrió un error en la simulación: " + e.getMessage());
        }
        
        }
    

    private static criptoMoneda[] pick10Random(List<criptoMoneda> lista) {
        int n = Math.min(10, lista.size());
        criptoMoneda[] result = new criptoMoneda[n];
        Random rnd = new Random();
        
        for (int i = 0; i < n; i++) {
            result[i] = lista.get(rnd.nextInt(lista.size()));
        }
        return result;
    }

    private static void fluctuatePrices(criptoMoneda[] coins, double pct, Random rnd) {
        for (criptoMoneda c : coins) {
            try {
                double price = Double.parseDouble(c.getPrice_usd().replace(",", "."));
                double factor = 1.0 + (rnd.nextDouble() * 2 * pct - pct);
                double newPrice = Math.max(0.0001, price * factor);
                c.setPrice_usd(String.format("%.6f", newPrice));
            } catch (Exception e) {
                System.out.println("Error actualizando precio de " + c.getSymbol() + ": " + e.getMessage());
                logger.error("Error actualizando precio de " + c.getSymbol() + ": " + e.getMessage(),e);
            }
        }
    }

    private static void createRandomOrder(trader t, criptoMoneda[] coins, orderBook book, Random rnd) {
        criptoMoneda coin = coins[rnd.nextInt(coins.length)];
        transaccion.Type type = rnd.nextBoolean() ? transaccion.Type.buy : transaccion.Type.sell;
        double quantity = 0.01 + rnd.nextDouble() * 0.1; 
        
        try {
            double price = Double.parseDouble(coin.getPrice_usd().replace(",", "."));
            transaccion tx = new transaccion(t.getid(), type, coin.getSymbol(), price, quantity);
            book.addOrder(tx);
            
            System.out.println("  " + t.getname() + " ordena " + type + " " + 
                             String.format("%.4f", quantity) + " " + coin.getSymbol());
        } catch (Exception e) {
            System.out.println("  Error creando orden para " + t.getname());
        }
    }
}
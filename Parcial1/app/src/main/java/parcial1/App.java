package parcial1;

import java.util.List;
import java.util.Random;

import parcial1.model.ApiCripto;
import parcial1.model.criptoMoneda;
import parcial1.model.orderBook;
import parcial1.model.trader;
import parcial1.model.transaccion;
import parcial1.service.TransactionProcessor;

public class App {

    public static void main(String[] args) throws Exception {

        ApiCripto api = new ApiCripto("https://api.coinlore.net/api/tickers/");
        List<criptoMoneda> lista = api.Obtener();

        criptoMoneda[] coins = pick10Random(lista);

        trader alice = new trader("u1", "Alice", 5_000_000, 4000);
        trader bob = new trader("u2", "Bob", 8_000_000, 4000);
        trader[] traders = {alice, bob};

        orderBook book = new orderBook();
        TransactionProcessor processor = new TransactionProcessor();

        Random rnd = new Random();
        System.out.println("=== INICIANDO SIMULACIÓN ===");
        
        for (int turno = 1; turno <= 10; turno++) {
            System.out.println("\n--- TURNO " + turno + " ---");

            fluctuatePrices(coins, 0.05, rnd);

            System.out.println("Precios actuales:");
            for (int i = 0; i < Math.min(3, coins.length); i++) {
                criptoMoneda c = coins[i];
                System.out.println("  " + c.getSymbol() + ": $" + c.getPrice_usd());
            }

            for (trader t : traders) {
                createRandomOrder(t, coins, book, rnd);
            }

            System.out.println("Procesando órdenes:");
            while (!book.isEmpty()) {
                processor.processOrder(book, traders, coins);
            }
        }

        System.out.println("\n=== RESUMEN FINAL ===");
        for (trader t : traders) {
            System.out.println(t);
            System.out.println("  Portafolio:");
            t.getWallet().forEach(holding -> System.out.println("    " + holding));
            System.out.println("  Total transacciones: " + t.getHistory().size());
            System.out.println();
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
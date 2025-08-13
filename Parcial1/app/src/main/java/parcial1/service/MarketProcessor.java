package parcial1.service;

import java.util.Random;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import parcial1.model.criptoMoneda;

public class MarketProcessor {

    private final Bag<criptoMoneda> marketCoins;

    public MarketProcessor() {
        this.marketCoins = new HashBag<>();
    }

    public void addCoin(criptoMoneda coin) {
        marketCoins.add(coin);
    }

    public void addAll(Iterable<criptoMoneda> coins) {
        for (criptoMoneda c : coins) marketCoins.add(c);
    }

    public void fluctuateAll(double pct, Random rnd) {
        for (criptoMoneda c : marketCoins) {
            try {
                double price = Double.parseDouble(c.getPrice_usd());
                double factor = 1.0 + (rnd.nextDouble() * 2 * pct - pct);
                double newPrice = Math.max(0.0001, price * factor);
                c.setPrice_usd(String.format("%.6f", newPrice));
            } catch (NumberFormatException ignored) { }
        }
    }

    public void showMarket() {
        System.out.println("=== Mercado de Criptomonedas ===");
        for (criptoMoneda c : marketCoins) {
            System.out.println(c);
        }
        System.out.println("(total en bolsa: " + marketCoins.size() + ")");
    }

    public Bag<criptoMoneda> getMarketCoins() {
        return marketCoins;
    }
}
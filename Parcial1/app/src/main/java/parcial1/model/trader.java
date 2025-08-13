package parcial1.model;

import java.util.Stack;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import parcial1.interfaces.Itrader;

public class trader implements Itrader {

    private final String id;
    private final String name;
    private double balance;          
    private double copPerUsd;        

    private final Stack<transaccion> history = new Stack<>();
    private final Bag<holding> wallet = new HashBag<>();

    public trader(String id, String name, double balance, double copPerUsd) {
        this.id = id;
        this.name = name;
        this.balance = balance;      
        this.copPerUsd = copPerUsd;  
    }

    public String getid() { return id; }
    public String getname() { return name; }
    public double getBalance() { return balance; }
    public Bag<holding> getWallet() { return wallet; }
    public Stack<transaccion> getHistory() { return history; }
    public double getCopPerUsd() { return copPerUsd; }
    public void setCopPerUsd(double copPerUsd) { this.copPerUsd = copPerUsd; }

    @Override
    public boolean buy(criptoMoneda coin, double quantity) {
        return buy(coin, quantity, this.copPerUsd);
    }

    @Override
    public boolean sell(criptoMoneda coin, double quantity) {
        return sell(coin, quantity, this.copPerUsd);
    }

    public boolean buy(criptoMoneda coin, double quantity, double copPerUsd) {
        if (coin == null || quantity <= 0 || copPerUsd <= 0) return false;

        double priceUsd = toDouble(coin.getPrice_usd()); 
        if (priceUsd <= 0) return false;

        double balanceUsd = balance / copPerUsd;

        double costUsd = priceUsd * quantity;
        if (costUsd > balanceUsd) return false;

        balance -= costUsd * copPerUsd;

        holding h = findHolding(coin.getSymbol());
        if (h == null) wallet.add(new holding(coin.getSymbol(), quantity));
        else h.setQuantity(h.getQuantity() + quantity);

        history.push(new transaccion(id, transaccion.Type.buy, coin.getSymbol(), priceUsd, quantity));
        return true;
    }

    public boolean sell(criptoMoneda coin, double quantity, double copPerUsd) {
        if (coin == null || quantity <= 0 || copPerUsd <= 0) return false;

        double priceUsd = toDouble(coin.getPrice_usd()); 
        if (priceUsd <= 0) return false;

        holding h = findHolding(coin.getSymbol());
        if (h == null || quantity > h.getQuantity()) return false;

        h.setQuantity(h.getQuantity() - quantity);
        if (h.getQuantity() == 0) wallet.remove(h); 

        double revenueUsd = priceUsd * quantity;
        balance += revenueUsd * copPerUsd;

        history.push(new transaccion(id, transaccion.Type.sell, coin.getSymbol(), priceUsd, quantity));
        return true;
    }

    private holding findHolding(String symbol) {
        for (holding h : wallet) {
            if (h.getSymbol().equalsIgnoreCase(symbol)) {
                return h;
            }
        }
        return null;
    }

    private static double toDouble(String s) {
        if (s == null) return 0.0;
        try {
            return Double.parseDouble(s.replace(",", ".").trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @Override
    public String toString() {
        return "Trader " + name + " con saldo COP " + balance + " y " + history.size() + " transacciones";
    }
}
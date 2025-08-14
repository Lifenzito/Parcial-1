
package parcial1.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.Stack;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import parcial1.interfaces.Itrader;

public class trader implements Itrader {

    private static final Logger logger = LogManager.getLogger(trader.class.getName());


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
        try {
            if (coin == null || quantity <= 0 || copPerUsd <= 0) {
                logger.error("Compra inválida: parámetros incorrectos");
                throw new IllegalArgumentException("Compra inválida: parámetros incorrectos");
            }
            double priceUsd = toDouble(coin.getPrice_usd()); 
            if (priceUsd <= 0) {
                logger.error("Compra inválida: precio USD incorrecto");
                throw new IllegalArgumentException("Compra inválida: precio USD incorrecto");
            }
            double balanceUsd = balance / copPerUsd;
            double costUsd = priceUsd * quantity;
            if (costUsd > balanceUsd) {
                logger.error("Compra inválida: saldo insuficiente");
                throw new IllegalArgumentException("Compra inválida: saldo insuficiente");
            }
            balance -= costUsd * copPerUsd;
            holding h = findHolding(coin.getSymbol());
            if (h == null) wallet.add(new holding(coin.getSymbol(), quantity));
            else h.setQuantity(h.getQuantity() + quantity);
            history.push(new transaccion(id, transaccion.Type.buy, coin.getSymbol(), priceUsd, quantity));
            logger.info("Compra realizada: " + quantity + " de " + coin.getSymbol());
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Error en compra: " + e.getMessage(),e);
            return false;
        }
    }

    public boolean sell(criptoMoneda coin, double quantity, double copPerUsd) {
        try {
            if (coin == null || quantity <= 0 || copPerUsd <= 0) {
                logger.error("Venta inválida: parámetros incorrectos");
                throw new IllegalArgumentException("Venta inválida: parámetros incorrectos");
            }
            double priceUsd = toDouble(coin.getPrice_usd()); 
            if (priceUsd <= 0) {
                logger.error("Venta inválida: precio USD incorrecto");
                throw new IllegalArgumentException("Venta inválida: precio USD incorrecto");
            }
            holding h = findHolding(coin.getSymbol());
            if (h == null || quantity > h.getQuantity()) {
                logger.error("Venta inválida: cantidad insuficiente en portafolio");
                throw new IllegalArgumentException("Venta inválida: cantidad insuficiente en portafolio");
            }
            h.setQuantity(h.getQuantity() - quantity);
            if (h.getQuantity() == 0) wallet.remove(h); 
            double revenueUsd = priceUsd * quantity;
            balance += revenueUsd * copPerUsd;
            history.push(new transaccion(id, transaccion.Type.sell, coin.getSymbol(), priceUsd, quantity));
            logger.info("Venta realizada: " + quantity + " de " + coin.getSymbol());
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Error en venta: " + e.getMessage(),e);
            return false;
        }
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
        return "Trader " + name + " con saldo COP " + balance + " y " + history + " transacciones";
    }
}
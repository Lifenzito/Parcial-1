package parcial1.service;

import parcial1.model.criptoMoneda;
import parcial1.model.orderBook;
import parcial1.model.trader;
import parcial1.model.transaccion;

public class TransactionProcessor {

    public boolean processOrder(orderBook book, trader[] traders, criptoMoneda[] coins) {
        if (book.isEmpty()) return false;

        transaccion tx = book.removeOrder();
        trader who = findTrader(traders, tx.getTraderId());
        criptoMoneda coin = findCoin(coins, tx.getSymbol());

        if (who == null || coin == null) return false;

        boolean success;
        if (tx.getType() == transaccion.Type.buy) {
            success = who.buy(coin, tx.getQuantity());
        } else {
            success = who.sell(coin, tx.getQuantity());
        }

        if (success) {
            System.out.println("Transacción procesada: " + who.getname() + " " + 
                             tx.getType() + " " + tx.getQuantity() + " " + tx.getSymbol());
        } else {
            System.out.println("Transacción falló: " + who.getname() + " " + 
                             tx.getType() + " " + tx.getQuantity() + " " + tx.getSymbol());
        }

        return success;
    }

    private trader findTrader(trader[] traders, String id) {
        for (trader t : traders) {
            if (t.getid().equals(id)) return t;
        }
        return null;
    }

    private criptoMoneda findCoin(criptoMoneda[] coins, String symbol) {
        for (criptoMoneda c : coins) {
            if (c.getSymbol().equals(symbol)) return c;
        }
        return null;
    }
}

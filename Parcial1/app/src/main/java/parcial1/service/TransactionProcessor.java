package parcial1.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import parcial1.model.criptoMoneda;
import parcial1.model.orderBook;
import parcial1.model.trader;
import parcial1.model.transaccion;

public class TransactionProcessor {

    private static final Logger logger = LogManager.getLogger(TransactionProcessor.class.getName());


    public boolean processOrder(orderBook book, trader[] traders, criptoMoneda[] coins) {
        try {
            if (book.isEmpty()) {
                logger.error("No hay órdenes en el libro para procesar");
                throw new IllegalArgumentException("No hay órdenes en el libro para procesar");
            }
            transaccion tx = book.removeOrder();
            trader who = findTrader(traders, tx.getTraderId());
            criptoMoneda coin = findCoin(coins, tx.getSymbol());
            if (who == null) {
                logger.error("Trader no encontrado: " + tx.getTraderId());
                throw new IllegalArgumentException("Trader no encontrado: " + tx.getTraderId());
            }
            if (coin == null) {
                logger.error("Criptomoneda no encontrada: " + tx.getSymbol());
                throw new IllegalArgumentException("Criptomoneda no encontrada: " + tx.getSymbol());
            }
            boolean success;
            if (tx.getType() == transaccion.Type.buy) {
                success = who.buy(coin, tx.getQuantity());
            } else {
                success = who.sell(coin, tx.getQuantity());
            }
            if (success) {
                logger.info("Transacción procesada: " + who.getname() + " " + tx.getType() + " " + tx.getQuantity() + " " + tx.getSymbol());
            } else {
                logger.error("Transacción falló: " + who.getname() + " " + tx.getType() + " " + tx.getQuantity() + " " + tx.getSymbol());
            }
            return success;
        } catch (IllegalArgumentException e) {
            logger.error("Error procesando orden: " + e.getMessage(),e);
            return false;
        }
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

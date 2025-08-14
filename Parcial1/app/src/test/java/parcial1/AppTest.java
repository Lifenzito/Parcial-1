
package parcial1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import parcial1.model.criptoMoneda;
import parcial1.model.orderBook;
import parcial1.model.trader;
import parcial1.model.transaccion;
import parcial1.service.TransactionProcessor;

class AppTest {


    @Test
    void testCompraValida() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.buy, "BTC", 1.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertTrue(proc.processOrder(book, traders, coins));
    }

    @Test
    void testVentaValida() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        t.buy(c, 2.0); 
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.sell, "BTC", 1.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertTrue(proc.processOrder(book, traders, coins));
    }

    @Test
    void testCompraExactaConTodoSaldo() {
        trader t = new trader("u1", "Test", 4000, 4000); // 1 USD
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.buy, "BTC", 1.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertTrue(proc.processOrder(book, traders, coins));
    }

    @Test
    void testVentaTotal() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        t.buy(c, 1.0);
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.sell, "BTC", 1.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertTrue(proc.processOrder(book, traders, coins));
        // Ya no debe tener BTC
        assertEquals(0, t.getWallet().size());
    }

   
    @Test
    void testCompraSaldoInsuficiente() {
        trader t = new trader("u1", "Test", 100, 4000); // saldo muy bajo
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1000.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.buy, "BTC", 1000.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertFalse(proc.processOrder(book, traders, coins));
    }

    @Test
    void testVentaSinMonedas() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.sell, "BTC", 1.0, 1.0));
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertFalse(proc.processOrder(book, traders, coins));
    }

    @Test
    void testTraderNoExiste() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("BTC");
        c.setPrice_usd("1.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u2", transaccion.Type.buy, "BTC", 1.0, 1.0)); // id no existe
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertFalse(proc.processOrder(book, traders, coins));
    }

    @Test
    void testMonedaNoExiste() {
        trader t = new trader("u1", "Test", 10000, 4000);
        criptoMoneda c = new criptoMoneda();
        c.setSymbol("ETH");
        c.setPrice_usd("1.0");
        orderBook book = new orderBook();
        book.addOrder(new transaccion("u1", transaccion.Type.buy, "BTC", 1.0, 1.0)); // símbolo no existe
        TransactionProcessor proc = new TransactionProcessor();
        criptoMoneda[] coins = {c};
        trader[] traders = {t};
        assertFalse(proc.processOrder(book, traders, coins));
    }
}


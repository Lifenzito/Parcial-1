package parcial1.model;
import java.util.LinkedList;
import java.util.Queue;

public class orderBook {
    private final Queue<transaccion> queue = new LinkedList<>();

    public boolean addOrder(transaccion tx) {
        return queue.add(tx); 
    }

    public transaccion removeOrder() {
        return queue.poll(); 
    }

    public transaccion peekOrder() {
        return queue.peek(); 
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int size() {
        return queue.size();
    }

@Override

    public String toString() {
    
    return "OrderBook con " + size() + " órdenes en espera";
}
}
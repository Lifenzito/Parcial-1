package parcial1.model;
import java.util.Stack;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;

import parcial1.interfaces.Itrader;
public class trader implements Itrader{

private final String id;
private final String name;
private double balance;
private final Stack<transaccion> history = new Stack<>();
private final Bag<holding> wallet = new HashBag();


public trader(String id, String name, double balance){

    this.id = id;
    this.name= name;
    this.balance = balance;

}
public String getid(){
    return id;
}
public String getname(){
    return name;
}
public double getBalance(){
    return balance;

}

public Bag<holding> getWallet(){
    return wallet;
}
public Stack<transaccion> getHistory(){
    return history;


}

@Override

public boolean buy(criptoMoneda coin, double quantity){

double cost = Integer.parseInt(coin.getPrice_usd()) * quantity; 

if (quantity <= 0 || cost > balance) {

    return false;
    
}
balance -= cost;

holding h = findHolding(coin.getSymbol());
if(h == null ) wallet.add(new holding(coin.getSymbol(), quantity));
else h.setQuantity(h.getQuantity() + quantity);
history.push(new transaccion(id,transaccion.Type.buy ,coin.getSymbol(),Integer.parseInt(coin.getPrice_usd()) , quantity));
return true;



}

private holding findHolding(String symbol){

    for (holding h : wallet) {

        if (h.getSymbol().equalsIgnoreCase(symbol)) {

            return h;
            
        }
        
    }
    return null;

}

@Override
public boolean sell(criptoMoneda coin, double quantity){


holding h = findHolding(coin.getSymbol());
if(h == null || quantity <= 0|| quantity > h.getQuantity()) return false;
 
double revenue = Integer.parseInt(coin.getPrice_usd()) * quantity;
h.setQuantity(h.getQuantity() - quantity);
if(h.getQuantity() == 0) wallet.remove(h);
balance += revenue;

history.push(new transaccion(id, transaccion.Type.sell, coin.getSymbol(), quantity, Integer.parseInt(coin.getPrice_usd())));

return true;



}


}






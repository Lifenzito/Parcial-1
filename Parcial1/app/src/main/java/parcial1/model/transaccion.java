package parcial1.model;

public class transaccion {

    public enum Type{buy , sell}

    private final String traderId;
    private final String symbol;
    private final double priceUsd;
    private final double quantity;
    private final Type type;


    public transaccion(String traderId,Type type, String symbol, double priceUsd, double quantity){

        this.traderId = traderId;
        this.symbol = symbol;
        this.priceUsd = priceUsd;
        this.quantity = quantity;
        this.type = type;


    }

    @Override

    public String toString(){
        return "el trader " + traderId + " compro " + quantity + " de " + symbol + " por el precio de " + priceUsd + " USD " + " el tipo de la transaccion es : " + type;
    }


}

package parcial1.model;


public class holding {

    private String symbol;
    private double quantity;

    public holding(String symbol, double quantity){


        this.symbol = symbol;
        this.quantity = quantity;

    }

    public String getSymbol(){
        return symbol;
    }
    public double getQuantity(){
        return quantity;
    }
    public void setQuantity(double quantity){

        this.quantity = quantity;

    }

    @Override
    public String toString(){
        return " se tiene " + quantity + " de " + symbol;
    }

}

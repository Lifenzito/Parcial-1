package parcial1.interfaces;

import parcial1.model.criptoMoneda;

public interface Itrader {

    boolean buy(criptoMoneda coin, double quantity);
    boolean sell (criptoMoneda coin, double quantity);


}

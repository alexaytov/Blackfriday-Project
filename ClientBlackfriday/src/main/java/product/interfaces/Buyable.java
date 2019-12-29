package product.interfaces;

import exceptions.NotEnoughQuantity;
import user.interfaces.User;

public interface Buyable {
    
    void buy(User user, int quantity) throws NotEnoughQuantity;

    double getPrice();

    void setPrice(double price);
    
}

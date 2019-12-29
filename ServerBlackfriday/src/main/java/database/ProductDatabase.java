package database;

import database.IO.JSONReader;
import database.IO.JSONWriter;
import exceptions.NotFoundException;
import product.Product;

public class ProductDatabase extends BaseUserDatabase<Product> {

    private String filename;

    public ProductDatabase(String fileName) {
        super(JSONReader.readProducts(fileName));
        this.filename = fileName;
    }

    public Product getByName(String name) throws NotFoundException {
        Product product = super.getData().get(name);
        if (product == null) {
            throw new NotFoundException();
        }
        return product;
    }

    @Override
    public synchronized void write(Product data) {
        super.getData().put(data.getName(), data);
        this.saveAllChanges();
    }

    @Override
    public boolean contains(Product data) {
        return super.getData().containsKey(data.getName());
    }

    @Override
    public synchronized void delete(String productName) throws NotFoundException {
        if (super.getData().remove(productName) == null) {
            throw new NotFoundException();
        }
        this.saveAllChanges();
    }

    @Override
    public void saveAllChanges() {
        JSONWriter.writeProducts(super.getData(), this.filename);
    }
}

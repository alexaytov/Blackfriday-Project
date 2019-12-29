package database;

import database.IO.JSONReader;
import database.IO.JSONWriter;
import exceptions.NotFoundException;
import store.earnings.Purchase;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static validator.Validator.*;


public class PurchaseDatabase {

    private String fileName;
    private Map<String, List<Purchase>> data;

    public PurchaseDatabase(String fileName) throws IOException {
        this.setData(JSONReader.readPurchases(fileName));
        this.fileName = fileName;
    }


    private void setData(Map<String, List<Purchase>> data) {
        validateMap(data);
        this.data = data;
    }


    /**
     * Adds purchase to the (@code data)
     *
     * @param purchase purchase to be added
     */
    public synchronized void write(Purchase purchase) {
        String userName = purchase.getUserName();
        if (this.data.containsKey(userName)) {
            this.data.get(userName).add(purchase);
        } else {
            this.data.put(userName, new ArrayList<>());
            this.data.get(userName).add(purchase);
        }
        this.saveAllChanges();
    }

    /**
     * Deleted purchase from (@code data)
     *
     * @param purchase the purchase to be deleted
     * @return if the purchase was removed
     * @throws NotFoundException if the purchase wasn't found
     */
    public synchronized boolean delete(Purchase purchase) throws NotFoundException {
        try {
            boolean isRemoved = this.data.get(purchase.getUserName()).remove(purchase);
            this.saveAllChanges();
            return isRemoved;
        } catch (NullPointerException ex) {
            throw new NotFoundException();
        }
    }

    /**
     * @param startDate the beginning of the period
     * @param endDate   the end of the period
     * @return all the purchases between the (@code startDate) and (@code endDate)
     */
    public List<Purchase> getPurchases(LocalDate startDate, LocalDate endDate) {
        List<Purchase> validPurchases = new ArrayList<>();
        for (List<Purchase> listOfPurchases : this.data.values()) {
            for (Purchase purchase : listOfPurchases) {
                LocalDate purchaseDate = purchase.getDate().toLocalDate();
                if (purchaseDate.isAfter(startDate) && purchaseDate.isBefore(endDate)) {
                    validPurchases.add(purchase);
                }
            }

        }
        return validPurchases;
    }

    /**
     * @param year the year purchases happened
     * @return all purchasers happened in that (@code year)
     */
    public List<Purchase> getPurchases(int year) {
        validateYear(year);
        List<Purchase> validPurchases = new ArrayList<>();
        for (List<Purchase> purchaseList : this.data.values()) {
            for (Purchase purchase : purchaseList) {
                if (purchase.getDate().toLocalDate().getYear() == year) {
                    validPurchases.add(purchase);
                }
            }
        }
        return validPurchases;
    }

    /**
     * @param month the month in which the purchases happened
     * @param year  the year in which the purchases happened
     * @return all purchases that happened on (@code month)
     */
    public List<Purchase> getPurchases(int month, int year) {
        validateMonth(month);
        List<Purchase> validPurchases = new ArrayList<>();
        for (List<Purchase> purchaseList : this.data.values()) {
            for (Purchase purchase : purchaseList) {
                LocalDate purchaseDate = purchase.getDate().toLocalDate();
                if (purchaseDate.getYear() == year && purchaseDate.getMonth().getValue() == month) {
                    validPurchases.add(purchase);
                }
            }
        }
        return validPurchases;
    }

    /**
     * @param date the date in which the purchase happened
     * @return purchases that happened on (@code date)
     */
    public List<Purchase> getPurchases(LocalDate date) {
        List<Purchase> validPurchases = new ArrayList<>();
        for (Map.Entry<String, List<Purchase>> listEntry : this.data.entrySet()) {
            for (Purchase purchase : listEntry.getValue()) {
                if (purchase.getDate().toLocalDate().equals(date)) {
                    validPurchases.add(purchase);
                }
            }
        }
        return validPurchases;
    }

    private void saveAllChanges() {
        JSONWriter.writePurchase(this.data, fileName);
    }
}

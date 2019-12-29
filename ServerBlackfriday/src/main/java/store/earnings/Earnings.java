package store.earnings;

import database.PurchaseDatabase;

import java.time.LocalDate;
import java.util.List;

public class Earnings {

    private PurchaseDatabase purchaseDatabase;

    public Earnings(PurchaseDatabase purchaseDatabase) {
        this.purchaseDatabase = purchaseDatabase;
    }

    /**
     * Adds a purchase to the purchase database
     *
     * @param purchase the purchase added to the purchaseDatabase
     */
    public void logPurchase(Purchase purchase) {
        this.purchaseDatabase.write(purchase);
    }

    /**
     * @param year the year which we want to get all earnings from
     * @return all the earnings which happened in that year
     */
    public double getEarnings(int year) {
        List<Purchase> purchases = this.purchaseDatabase.getPurchases(year);
        return purchases.stream()
                .mapToDouble(Purchase::getCost)
                .sum();

    }

    /**
     * @param month the month in which the earnings were made
     * @param year  the year in which the earnings were made
     * @return total earnings for the specified month and year
     */
    public double getEarnings(int month, int year) {
        List<Purchase> purchases = this.purchaseDatabase.getPurchases(month, year);
        return purchases.stream()
                .mapToDouble(Purchase::getCost)
                .sum();
    }

    /**
     * @param startDate the beginning of the period in which the earnings were made
     * @param endDate   the end of the period in which the earnings were made
     * @return all earnings made between the startDate and endDate exclusive
     */
    public double getEarnings(LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases = this.purchaseDatabase.getPurchases(startDate, endDate);
        return purchases.stream()
                .mapToDouble(Purchase::getCost)
                .sum();
    }

    /**
     * @param date the date in which the earnings were made
     * @return all earnings made on the given date
     */
    public double getEarnings(LocalDate date) {
        List<Purchase> purchases = this.purchaseDatabase.getPurchases(date);
        return purchases.stream()
                .mapToDouble(Purchase::getCost)
                .sum();

    }
}

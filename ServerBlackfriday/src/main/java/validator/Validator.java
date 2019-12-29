package validator;

import common.ExceptionMessages;

import java.time.ZonedDateTime;
import java.util.Map;

public class Validator {

    public static void validateName(String firstName) {
        if (firstName == null || firstName.trim().length() == 0) {
            throw new IllegalArgumentException(ExceptionMessages.NAME_NULL_OR_EMPTY);
        }
    }

    public static void validateAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException(ExceptionMessages.AGE_MUST_BE_POSITIVE_NUMBER);
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.trim().length() == 0) {
            throw new IllegalArgumentException(ExceptionMessages.PASSWORD_NULL_OR_EMPTY);
        }
    }


    public static void validateDate(ZonedDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException(ExceptionMessages.DATE_NULL);
        }
    }

    public static void validateMinimumPrice(double minimumPrice) {
        if (minimumPrice <= 0) {
            throw new IllegalArgumentException(ExceptionMessages.MINIMUM_PRICE_MUST_BE_POSITIVE);
        }
    }

    public static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(ExceptionMessages.QUANTITY_ZERO_OR_NEGATIVE);
        }
    }

    public static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessages.DESCRIPTION_NULL_OR_EMPTY);
        }
    }

    public static void validatePrice(double price, double minimumPrice) {
        if (price <= 0) {
            throw new IllegalArgumentException(ExceptionMessages.PRICE_ZERO_NEGATIVE);
        }
        if (price < minimumPrice) {
            throw new IllegalArgumentException(ExceptionMessages.PRICE_BELOW_MINIMUM_PRICE);
        }
    }

    public static void validatePromotionalPricePercent(double promotionalPricePercent, double price, double minimumPrice) {
        double promotionalPrice = price * (1 - promotionalPricePercent / 100);
        if (promotionalPrice <= 0) {
            throw new IllegalArgumentException(ExceptionMessages.PROMOTIONAL_PRICE_ZERO_NEGATIVE);
        }
        if (promotionalPrice < minimumPrice) {
            throw new IllegalArgumentException(ExceptionMessages.PROMOTIONAL_PRICE_BELOW_MINIMUM_PRICE);
        }
    }

    public static void validateMonth(int month) {
        if (month <= 0 || month > 12) {
            throw new IllegalArgumentException(String.format(ExceptionMessages.MONTH_MUST_BE_BETWEEN_0_1, month));
        }
    }

    public static void validateYear(int year) {
        if (year <= 0) {
            throw new IllegalArgumentException(String.format(ExceptionMessages.YEAR_MUST_BE_POSITIVE, year));
        }
    }

    public static void validateMap(Map map){
        if(map == null){
            throw new NullPointerException("Map can't be null");
        }
    }

}

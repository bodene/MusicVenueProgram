package util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    public static String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(value);
    }

    public static String formatNumber(int value) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        return numberFormat.format(value);
    }
}
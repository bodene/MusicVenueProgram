package util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting numbers as currency or standard number strings.
 * <p>
 * The {@code NumberUtils} class provides static helper methods to format numeric values according
 * to the US locale. This class follows the utility class pattern and should not be instantiated.
 * </p>
 *
 * @author Bodene Downie
 * @version 1.0
 */
public class NumberUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private NumberUtils() {}

    /**
     * Formats a double value as currency using the US locale.
     * <p>
     * For example, the value 1234.56 will be formatted as "$1,234.56".
     * </p>
     *
     * @param value the double value to format
     * @return a formatted currency string
     */
    public static String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(value);
    }

    /**
     * Formats an integer value as a standard number string using the US locale.
     * <p>
     * For example, the value 1234567 will be formatted as "1,234,567".
     * </p>
     *
     * @param value the integer value to format
     * @return a formatted number string
     */
    public static String formatNumber(int value) {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        return numberFormat.format(value);
    }
}
package com.khmelyuk.memory.util;

import com.khmelyuk.memory.Memory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Tools to work with formats and outputs.
 *
 * @author Ruslan Khmelyuk
 */
public class FormatUtil {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public static String sizeAsString(int size) {
        if (size >= Memory.KB) {
            return getDivision(size, Memory.KB).toString() + "KB";
        } else if (size >= Memory.MB) {
            return getDivision(size, Memory.MB).toString() + "MB";
        } else if (size >= Memory.GB) {
            return getDivision(size, Memory.GB).toString() + "GB";
        }
        return String.valueOf(size);
    }

    public static BigDecimal getDivision(int value1, int value2) {
        if (value2 == 0 || value1 == 0) {
            return BigDecimal.ZERO;
        }
        double m = (double) value1 / (double) value2;
        return round(new BigDecimal(m));
    }

    public static BigDecimal getPercent(long num, long totalNum) {
        if (totalNum == 0 || num == 0) {
            return BigDecimal.ZERO;
        }
        double m = (double) num / (double) totalNum;
        return round(new BigDecimal(m).multiply(ONE_HUNDRED));
    }

    /**
     * Rounds to 2 digits after point.
     *
     * @param value the value to round.
     * @return the rounded value.
     */
    public static BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

}

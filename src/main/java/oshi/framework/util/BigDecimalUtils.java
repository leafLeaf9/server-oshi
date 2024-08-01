package oshi.framework.util;


import oshi.framework.constant.StringPool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BigDecimalUtils {

    public static double add(double... array) {
        BigDecimal b1 = new BigDecimal(Double.toString(0));
        BigDecimal b2;
        for (double augend : array) {
            b2 = BigDecimal.valueOf(augend);
            b1 = b1.add(b2);
        }
        return b1.doubleValue();

    }

    public static double subtract(double b, double... array) {
        BigDecimal b1 = BigDecimal.valueOf(b);
        BigDecimal b2;
        for (double subtrahend : array) {
            b2 = BigDecimal.valueOf(subtrahend);
            b1 = b1.subtract(b2);
        }
        return b1.doubleValue();
    }

    public static BigDecimal absSubtract(double b, double... array) {
        BigDecimal b1 = BigDecimal.valueOf(b);
        BigDecimal b2;
        for (double subtrahend : array) {
            b2 = BigDecimal.valueOf(subtrahend);
            b1 = b1.subtract(b2);
        }
        return BigDecimal.valueOf(Math.abs(b1.doubleValue()));
    }

    public static double multiply(int a, double b) {
        BigDecimal b1 = BigDecimal.valueOf(a);
        BigDecimal b2 = BigDecimal.valueOf(b);
        b1 = b1.multiply(b2);
        return b1.doubleValue();
    }

    public static double multiply(double a, double b) {
        BigDecimal a1 = BigDecimal.valueOf(a);
        BigDecimal b1 = BigDecimal.valueOf(b);
        a1 = a1.multiply(b1);
        return a1.doubleValue();
    }

    public static Double divide(double a, double b, int scale) {
        BigDecimal a1 = BigDecimal.valueOf(a);
        BigDecimal b1 = BigDecimal.valueOf(b);
        if (b1.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        a1 = a1.divide(b1, scale, RoundingMode.HALF_UP);
        return a1.doubleValue();
    }

    public static BigDecimal divideScale(long a, long b, int scale) {
        if (b == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal a1 = BigDecimal.valueOf(a);
        BigDecimal b1 = BigDecimal.valueOf(b);
        return a1.divide(b1, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(long a, long b) {
        return divideScale(a, b, 3);
    }

    public static double scale(double a, int scale) {
        return BigDecimal.valueOf(a).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static double scale(Double a, int scale) {
        if (a == null) {
            return 0D;
        }
        return BigDecimal.valueOf(a).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    public static String format(double a, int scale) {
        return BigDecimal.valueOf(a).setScale(scale, RoundingMode.HALF_UP).toString();
    }

    private static String format2String(double value, int scale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(scale);
        numberFormat.setMinimumFractionDigits(scale);
        numberFormat.setGroupingUsed(false);
        return numberFormat.format(value);
    }

    public static double getEventCentralPoint(double beginPosition, double endPosition) {
        return BigDecimal.valueOf(beginPosition).add(BigDecimal.valueOf(endPosition))
                .divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP).doubleValue();
    }

    public static BigDecimal getCentralPoint(BigDecimal beginPosition, BigDecimal endPosition) {
        return beginPosition.add(endPosition)
                .divide(BigDecimal.valueOf(2), 3, RoundingMode.HALF_UP);
    }

    public static String getRate(Integer dividend, Integer divisor) {
        // 分子为null 或者分母为null 分母为0 时 返回-
        if (dividend == null || nonNull(divisor) == 0) {
            return StringPool.DASH;
        }
        return getRate(BigDecimal.valueOf(dividend), BigDecimal.valueOf(divisor));
    }

    public static String getChangeRate(Integer dividend, Integer divisor) {
        if (dividend == null || nonNull(divisor) == 0) {
            return StringPool.DASH;
        }
        return getRate(dividend - divisor, divisor);
    }

    public static String getChangeRate(Long dividend, Long divisor) {
        if (dividend == null || nonNull(divisor) == 0) {
            return StringPool.DASH;
        }
        return getRate(dividend - divisor, divisor);
    }

    /**
     * @param dividend 被除数(分子)
     * @param divisor  除数(分母)
     * @return {@link String}
     */
    public static String getRate(Long dividend, Long divisor) {
        // 分子为null 或者分母为null 分母为0 时 返回-
        if (dividend == null || nonNull(divisor) == 0) {
            return StringPool.DASH;
        }
        return getRate(BigDecimal.valueOf(dividend), BigDecimal.valueOf(divisor));
    }

    public static BigDecimal getRateDecimal(Long dividend, Long divisor) {
        if (dividend == null || nonNull(divisor) == 0) {
            return BigDecimal.ZERO;
        }
        return getRateDecimal(BigDecimal.valueOf(dividend), BigDecimal.valueOf(divisor));
    }

    public static String getRate(BigDecimal dividend, BigDecimal divisor) {
        // 分子为null 或者分母为null 分母为0 时 返回-
        if (dividend == null || nonNull(divisor).compareTo(BigDecimal.ZERO) == 0) {
            return StringPool.DASH;
        }
        return dividend.multiply(BigDecimal.valueOf(100))
                .divide(divisor, 2, RoundingMode.HALF_UP) + StringPool.PERCENT;
    }

    public static BigDecimal getRateDecimal(BigDecimal dividend, BigDecimal divisor) {
        // 分子为null 或者分母为null 分母为0 时 返回null
        if (dividend == null || nonNull(divisor).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return dividend.multiply(BigDecimal.valueOf(100)).divide(divisor, 2, RoundingMode.HALF_UP);
    }

    public static String getRate(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return StringPool.DASH;
        }
        return bigDecimal.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP) + StringPool.PERCENT;
    }

    public static String buildCountAndRate(Long count, String rate) {
        return (count == null ? StringPool.DASH : count) + StringPool.LEFT_BRACKET + rate + StringPool.RIGHT_BRACKET;
    }

    public static BigDecimal nonNull(BigDecimal e) {
        return e == null ? BigDecimal.ZERO : e;
    }

    public static Long nonNull(Long e) {
        return e == null ? 0L : e;
    }

    public static Long nonNull(Integer e) {
        return e == null ? 0L : e;
    }

    public static Double nonNull(Double e) {
        return e == null ? 0D : e;
    }

    public static String zeroComplete(int time) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(time);
    }

    public static String zeroComplete(long time) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(time);
    }

	/*public static BigDecimal oidValueToBigDecimal(String str) {
		if (SNMPConstant.NO_SUCH_OBJECT.equalsIgnoreCase(str) || StringUtils.isEmpty(str)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(str);
	}*/

}
